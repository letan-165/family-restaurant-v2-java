package family.main.project.internal.order.dto.request;

import family.main.project.common.enums.ItemStatus;
import family.main.project.common.enums.ItemType;
import lombok.Data;

@Data
public class ItemSaveRequest {
    String name;
    ItemStatus status;
    ItemType type;
    Integer price;
    String picture;
    String description;
}