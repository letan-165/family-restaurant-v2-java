package family.main.project.internal.order.service;

import family.main.project.common.exception.AppException;
import family.main.project.common.exception.ErrorCode;
import family.main.project.internal.order.dto.request.OrderUpdateInfoRequest;
import family.main.project.internal.order.dto.response.OrderUpdateInfoResponse;
import family.main.project.internal.order.entity.UserOrder;
import family.main.project.internal.order.mapper.OrderMapper;
import family.main.project.internal.order.repository.UserOrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserOrderService {
    UserOrderRepository userOrderRepository;
    OrderMapper orderMapper;

    public OrderUpdateInfoResponse updateInfo(Long orderId, OrderUpdateInfoRequest request) {
        UserOrder userOrder = userOrderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NO_EXISTS));

        orderMapper.updateOrderFromRequest(userOrder, request);

        UserOrder update = userOrderRepository.save(userOrder);

        return orderMapper.toOrderUpdateInfoResponse(update);
    }
}
