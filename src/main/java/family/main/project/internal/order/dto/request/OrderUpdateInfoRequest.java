package family.main.project.internal.order.dto.request;

import lombok.Data;

@Data
public class OrderUpdateInfoRequest {
    String receiverName;
    String phone;
    String address;
}
