package family.main.project.internal.order.dto.response;

import lombok.Data;

@Data
public class OrderUpdateInfoResponse {
    Long orderId;
    String receiverName;
    String phone;
    String address;
}
