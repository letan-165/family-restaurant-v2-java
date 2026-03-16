package family.main.project.internal.order.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class OrderCreateRequest {
    String userId;
    String receiverName;
    String phone;
    String address;
    String note;

    List<OrderItemRequest> items;
}
