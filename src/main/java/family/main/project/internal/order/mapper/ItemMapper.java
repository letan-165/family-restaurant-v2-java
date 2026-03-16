package family.main.project.internal.order.mapper;

import family.main.project.internal.order.dto.request.ItemSaveRequest;
import family.main.project.internal.order.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemMapper {
    Item toItem(ItemSaveRequest request);

    void updateItemFromRequest(@MappingTarget Item item, ItemSaveRequest request);
}