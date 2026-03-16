package family.main.project.internal.order.mapper;

import family.main.project.internal.order.dto.request.OrderUpdateInfoRequest;
import family.main.project.internal.order.dto.response.OrderUpdateInfoResponse;
import family.main.project.internal.order.entity.Order;
import family.main.project.internal.order.entity.UserOrder;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper {
    OrderUpdateInfoResponse toOrderUpdateInfoResponse(UserOrder userOrder);

    void updateOrderFromRequest(@MappingTarget UserOrder userOrder, OrderUpdateInfoRequest request);
}