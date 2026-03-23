package family.main.project.internal.order.service;

import family.main.project.common.enums.OrderStatus;
import family.main.project.common.exception.AppException;
import family.main.project.common.exception.ErrorCode;
import family.main.project.common.model.response.ItemResponse;
import family.main.project.internal.cart.entity.CartItem;
import family.main.project.internal.cart.entity.UserCart;
import family.main.project.internal.cart.repository.CartItemRepository;
import family.main.project.internal.cart.repository.CartRepository;
import family.main.project.internal.item.entity.Item;
import family.main.project.internal.item.mapper.ItemMapper;
import family.main.project.internal.item.repository.ItemRepository;
import family.main.project.internal.order.dto.request.OrderCreateRequest;
import family.main.project.internal.order.dto.request.OrderItemRequest;
import family.main.project.internal.order.dto.response.OrderDetailResponse;
import family.main.project.internal.order.dto.response.OrderUpdateStatusResponse;
import family.main.project.internal.order.entity.*;
import family.main.project.internal.order.repository.*;
import family.main.project.internal.user.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

    @CacheEvict(value = "user-order", key = "#result.userId")
    @Transactional
    public UserOrder createOrder(String userId, OrderCreateRequest request, boolean fromCart) {
        //Request quá dư để chuyển từ cart sang order

        //Check user
        if (!userRepository.existsById(userId))
            throw new AppException(ErrorCode.USER_NO_EXISTS);

        List<Long> itemIds = request.getItems().stream()
                .map(OrderItemRequest::getItemId)
                .toList();

        //FromCart
        if (fromCart) {
            UserCart cart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.CART_NO_EXISTS));
            Long cartId = cart.getId();
            if(cartItemRepository.countByCartIdAndItemIdIn(cartId, itemIds)!=itemIds.size())
                throw new AppException(ErrorCode.ITEM_CART_NO_EXISTS);
        }

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

        //FromCart
        if (fromCart)
            cartItemRepository.deleteAllByItemIdIn(itemIds);

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
                .collect(Collectors.toMap(OrderItem::getId, o -> o));

        List<Item> items = itemRepository.findAllById(orderItemMap.keySet());

        //MapItemResponse
        List<ItemResponse> itemResponses = items.stream().map(item -> {
           ItemResponse itemResponse = itemMapper.toItemResponse(item);
           OrderItem orderItem = orderItemMap.get(item.getId());

           itemResponse.setQuantity(orderItem.getQuantity());
           itemResponse.setPrice(orderItem.getPrice());
           itemResponse.setObjId(orderItem.getId());

           return itemResponse;
        }).toList();

        return OrderDetailResponse.builder()
                .user(userOrder)
                .order(order)
                .items(itemResponses)
                .build();
    }
}