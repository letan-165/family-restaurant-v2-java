package family.main.project.internal.order.service;

import family.main.project.common.exception.AppException;
import family.main.project.common.exception.ErrorCode;
import family.main.project.internal.order.dto.request.OrderUpdateInfoRequest;
import family.main.project.internal.order.dto.response.GetAllMyResponse;
import family.main.project.internal.order.dto.response.OrderResponse;
import family.main.project.internal.order.dto.response.OrderUpdateInfoResponse;
import family.main.project.internal.order.entity.OrderItem;
import family.main.project.internal.order.entity.Order;
import family.main.project.internal.order.entity.UserOrder;
import family.main.project.internal.item.mapper.ItemMapper;
import family.main.project.internal.order.mapper.OrderMapper;
import family.main.project.internal.order.repository.OrderItemRepository;
import family.main.project.internal.order.repository.OrderRepository;
import family.main.project.internal.order.repository.UserOrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserOrderService {

    UserOrderRepository userOrderRepository;
    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;

    OrderMapper orderMapper;
    ItemMapper itemMapper;


    @Cacheable(value = "user-order", key = "#userId")
    public GetAllMyResponse getAllMy(String userId, Pageable pageable) {
        //GetInfo
        List<UserOrder> userOrders = userOrderRepository.findAllByUserId(userId,pageable).getContent();

        //GetOrders
        List<Long> orderIds = userOrders.stream()
                .map(UserOrder::getOrderId)
                .toList();

        List<Order> orders = orderRepository.findAllById(orderIds);

        //MapOrders
        Map<Long, Order> orderMaps = orders.stream()
                .collect(Collectors.toMap(Order::getId, o -> o));

        List<OrderResponse> orderResponses = userOrders.stream().map(userOrder -> {
            Long orderId = userOrder.getOrderId();

            Order order = orderMaps.get(orderId);
            if (order == null) {
                throw new AppException(ErrorCode.ORDER_NO_EXISTS);
            }

            return OrderResponse.builder()
                    .order(order)
                    .user(userOrder)
                    .build();
        }).collect(Collectors.toList());


        return GetAllMyResponse.builder()
                .userId(userId)
                .orders(orderResponses)
                .build();
    }

    @CacheEvict(value = "user-order", key = "#result.userId")
    public OrderUpdateInfoResponse updateInfo(Long orderId, OrderUpdateInfoRequest request) {
        UserOrder userOrder = userOrderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NO_EXISTS));

        orderMapper.updateOrderFromRequest(userOrder, request);

        UserOrder update = userOrderRepository.save(userOrder);

        return orderMapper.toOrderUpdateInfoResponse(update);
    }

}
