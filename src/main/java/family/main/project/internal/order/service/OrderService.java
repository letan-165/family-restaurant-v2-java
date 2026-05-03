package family.main.project.internal.order.service;

import family.main.project.common.enums.ItemStatus;
import family.main.project.common.enums.OrderStatus;
import family.main.project.common.enums.UserStatus;
import family.main.project.common.exception.AppException;
import family.main.project.common.exception.ErrorCode;
import family.main.project.common.model.response.ItemObjResponse;
import family.main.project.internal.cart.entity.CartItem;
import family.main.project.internal.cart.entity.UserCart;
import family.main.project.internal.cart.repository.CartItemRepository;
import family.main.project.internal.cart.repository.CartRepository;
import family.main.project.internal.item.entity.Item;
import family.main.project.internal.item.mapper.ItemMapper;
import family.main.project.internal.item.repository.ItemRepository;
import family.main.project.internal.order.dto.request.OrderCreateFromCartRequest;
import family.main.project.internal.order.dto.request.OrderCreateRequest;
import family.main.project.internal.order.dto.request.OrderItemRequest;
import family.main.project.internal.order.dto.response.OrderDetailResponse;
import family.main.project.internal.order.dto.response.OrderUpdateStatusResponse;
import family.main.project.internal.order.entity.*;
import family.main.project.internal.order.mapper.OrderMapper;
import family.main.project.internal.order.repository.*;
import family.main.project.internal.user.entity.User;
import family.main.project.internal.user.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {

    OrderRepository orderRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;
    OrderItemRepository orderItemRepository;
    UserOrderRepository userOrderRepository;
    CartItemRepository cartItemRepository;
    CartRepository cartRepository;

    ItemMapper itemMapper;
    OrderMapper orderMapper;

    UserOrder createOrder(String userId, OrderCreateRequest request) {
        //Check user
        User user = userRepository.findById(userId)
                .orElseThrow(() ->new AppException(ErrorCode.USER_NO_EXISTS));

        if (user.getStatus().equals(UserStatus.BANNED))
            throw new AppException(ErrorCode.USER_NO_ACTIVE);

        List<Long> itemIds = request.getItems().stream()
                .map(OrderItemRequest::getItemId)
                .toList();

        //Set order
        Order order = Order.builder()
                .status(OrderStatus.PENDING)
                .note(request.getNote())
                .timeBooking(new Date())
                .total(0)
                .build();
        //Save order get OrderId
        order = orderRepository.save(order);

        //Init total $ item
        int total = 0;
        List<OrderItem> orderItems = new ArrayList<>();
        List<Item> items = itemRepository.findAllById(itemIds);
        Map<Long, Item> itemMap = items.stream().collect(Collectors.toMap(Item::getId,o->o));

        for (OrderItemRequest itemReq : request.getItems()) {

            Long itemId = itemReq.getItemId();
            Item item = itemMap.get(itemId);

            if (item==null)
                    throw new AppException(ErrorCode.ITEM_NO_EXISTS);

            if (!item.getStatus().equals(ItemStatus.ACTIVE))
                throw new AppException(ErrorCode.ITEM_NO_ACTIVE);


            int price = item.getPrice();
            item.setSold(item.getSold() + itemReq.getQuantity());

            OrderItem orderItem = OrderItem.builder()
                    .orderId(order.getId())
                    .itemId(itemId)
                    .quantity(itemReq.getQuantity())
                    .price(price)
                    .build();

            orderItems.add(orderItem);
            itemMap.put(itemId,item);
            total += price * itemReq.getQuantity();
        }

        order.setTotal(total);
        orderRepository.save(order);

        //Save item-order
        orderItemRepository.saveAll(orderItems);

        //Save item
        itemRepository.saveAll(itemMap.values());

        //Save user-order
        UserOrder userOrder = UserOrder.builder()
                .userId(userId)
                .orderId(order.getId())
                .receiverName(request.getReceiverName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .build();

        return userOrderRepository.save(userOrder);
    }

    @CacheEvict(value = "user-order", key = "#result.userId")
    @Transactional
    public UserOrder createFromIndex(String userId, OrderCreateRequest request){
        return createOrder(userId, request);
    }

    @CacheEvict(value = "user-order", key = "#result.userId")
    @Transactional
    public UserOrder createFromCart(Long cartId, OrderCreateFromCartRequest request){
        List<Long> cartItemIds = request.getItemCartIds();
        List<CartItem> cartItems = cartItemRepository.findAllById(cartItemIds);

        UserCart userCart = cartRepository.findById(cartId)
                .orElseThrow(()-> new AppException(ErrorCode.CART_NO_EXISTS));

        boolean allItemOnCart = cartItems.stream()
                .allMatch(item -> item.getCartId().equals(cartId));
        log.info("allItemOnCart{}",String.valueOf(allItemOnCart));
        log.info("cartItemIds{}",cartItemIds.toString());
        log.info("cartItems{}",cartItems.toString());
        if(cartItems.size()!=cartItemIds.size() || !allItemOnCart)
            throw new AppException(ErrorCode.ITEM_CART_NO_EXISTS);

        String userId = userCart.getUserId();
        List<OrderItemRequest> orderItemRequests = cartItems.stream()
                .map(orderMapper::toOrderItemRequest)
                .toList();

        OrderCreateRequest orderCreateRequest = orderMapper.toOrderCreateRequest(request);

        orderCreateRequest.setItems(orderItemRequests);

        UserOrder userOrder = createOrder(userId, orderCreateRequest);

        cartItemRepository.deleteAllById(cartItemIds);
        return userOrder;
    }

    @CacheEvict(value = "user-order", key = "#result.userId")
    public OrderUpdateStatusResponse updateStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NO_EXISTS));

        OrderStatus currentStatus = order.getStatus();

        if (currentStatus == OrderStatus.COMPLETED)
            throw new AppException(ErrorCode.ORDER_COMPLETED);

        switch (status) {
            case PENDING -> throw new AppException(ErrorCode.ORDER_PENDING_NO_UPDATE);

            case CONFIRMED, CANCELLED -> {
                if (currentStatus != OrderStatus.PENDING)
                    throw new AppException(ErrorCode.ORDER_NO_PENDING);
            }

            case COMPLETED -> {
                if (currentStatus != OrderStatus.CONFIRMED)
                    throw new AppException(ErrorCode.ORDER_NO_CONFIRMED);
                order.setTimeCompleted(new Date());
            }
        }

        order.setStatus(status);
        Order response = orderRepository.save(order);
        UserOrder userOrder = userOrderRepository.findByOrderId(response.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NO_EXISTS));

        return OrderUpdateStatusResponse.builder()
                .orderId(userOrder.getOrderId())
                .userId(userOrder.getUserId())
                .status(response.getStatus())
                .build();
    }

    public OrderDetailResponse getDetail(Long orderId) {
        //GetOrder
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new AppException(ErrorCode.ORDER_NO_EXISTS));

        //GetInfo
        UserOrder userOrder = userOrderRepository.findByOrderId(orderId)
                .orElseThrow(()-> new AppException(ErrorCode.ORDER_NO_EXISTS));

        //GetItem
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        Map<Long, OrderItem> orderItemMap = orderItems.stream()
                .collect(Collectors.toMap(OrderItem::getItemId, o -> o));

        List<Item> items = itemRepository.findAllById(orderItemMap.keySet());

        //MapItemResponse
        List<ItemObjResponse> itemObjResponses = items.stream().map(item -> {
           ItemObjResponse itemObjResponse = itemMapper.toItemResponse(item);
           OrderItem orderItem = orderItemMap.get(item.getId());

           orderMapper.updateOrderItemToItemObjResponse(orderItem, itemObjResponse);

           return itemObjResponse;
        }).toList();

        return OrderDetailResponse.builder()
                .user(userOrder)
                .order(order)
                .items(itemObjResponses)
                .build();
    }
}