package family.main.project.internal.order.dto.response;

import family.main.project.internal.order.entity.Order;
import family.main.project.internal.order.entity.UserOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    UserOrder user;
    Order order;
}
