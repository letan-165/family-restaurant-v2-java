package family.main.project.internal.item.mapper;

import family.main.project.common.model.response.ItemResponse;
import family.main.project.internal.item.dto.request.ItemSaveRequest;
import family.main.project.internal.item.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemMapper {
    Item toItem(ItemSaveRequest request);
    ItemResponse toItemResponse(Item item);

    void updateItemFromRequest(@MappingTarget Item item, ItemSaveRequest request);
}