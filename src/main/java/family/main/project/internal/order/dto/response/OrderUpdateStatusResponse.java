package family.main.project.internal.order.dto.response;

import family.main.project.common.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderUpdateStatusResponse {
    String userId;
    Long orderId;
    OrderStatus status;
}
