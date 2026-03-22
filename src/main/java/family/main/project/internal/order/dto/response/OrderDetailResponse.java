package family.main.project.internal.order.dto.response;

import family.main.project.common.model.response.ItemResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponse extends OrderResponse {
    List<ItemResponse> items;
}
