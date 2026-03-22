package family.main.project.internal.order.service;

import family.main.project.common.enums.OrderStatus;
import family.main.project.common.exception.AppException;
import family.main.project.common.exception.ErrorCode;
import family.main.project.common.model.response.ItemResponse;
import family.main.project.internal.item.entity.Item;
import family.main.project.internal.item.mapper.ItemMapper;
import family.main.project.internal.item.repository.ItemRepository;
import family.main.project.internal.order.dto.request.OrderCreateRequest;
import family.main.project.internal.order.dto.request.OrderItemRequest;
import family.main.project.internal.order.dto.response.OrderDetailResponse;
import family.main.project.internal.order.dto.response.OrderUpdateStatusResponse;
import family.main.project.internal.order.entity.*;
import family.main.project.internal.order.repository.*;
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
    ItemRepository itemRepository;
    OrderItemRepository orderItemRepository;
    UserOrderRepository userOrderRepository;
    ItemMapper itemMapper;

    @CacheEvict(value = "user-order", key = "#result.userId")
    @Transactional
    public UserOrder createOrder(String userId, OrderCreateRequest request) {
        Order order = Order.builder()
                .status(OrderStatus.PENDING)
                .note(request.getNote())
                .timeBooking(new Date())
                .total(0)
                .build();

        order = orderRepository.save(order);

        int total = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemReq : request.getItems()) {
            Item item = itemRepository.findById(itemReq.getItemId())
                    .orElseThrow(() -> new AppException(ErrorCode.ITEM_NO_EXISTS));

            int price = item.getPrice();

            OrderItem orderItem = OrderItem.builder()
                    .orderId(order.getId())
                    .itemId(itemReq.getItemId())
                    .quantity(itemReq.getQuantity())
                    .price(price)
                    .build();

            orderItems.add(orderItem);
            total += price;
        }

        order.setTotal(total);
        orderItemRepository.saveAll(orderItems);
        orderRepository.save(order);

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

           return itemResponse;
        }).toList();

        return OrderDetailResponse.builder()
                .user(userOrder)
                .order(order)
                .items(itemResponses)
                .build();
    }
}