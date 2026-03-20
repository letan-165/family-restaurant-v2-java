package family.main.project.internal.order.dto.response;

import family.main.project.internal.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    String receiverName;
    String phone;
    String address;
    Order order;
}
