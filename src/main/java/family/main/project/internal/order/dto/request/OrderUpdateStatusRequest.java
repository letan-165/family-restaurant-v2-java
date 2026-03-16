package family.main.project.internal.order.dto.request;

import family.main.project.common.enums.OrderStatus;
import lombok.Data;

import java.util.List;

@Data
public class OrderUpdateStatusRequest {
    OrderStatus status;
}
