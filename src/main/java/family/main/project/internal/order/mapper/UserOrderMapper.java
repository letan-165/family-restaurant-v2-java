package family.main.project.internal.order.mapper;

import family.main.project.internal.order.dto.response.OrderResponse;
import family.main.project.internal.order.entity.UserOrder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserOrderMapper {
    OrderResponse toOrderResponse(UserOrder userOrder);
}