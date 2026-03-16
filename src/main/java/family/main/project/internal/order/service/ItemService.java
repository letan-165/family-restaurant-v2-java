package family.main.project.internal.order.service;

import family.main.project.common.exception.AppException;
import family.main.project.common.exception.ErrorCode;
import family.main.project.internal.order.dto.request.ItemSaveRequest;
import family.main.project.internal.order.entity.Item;
import family.main.project.internal.order.entity.ItemOrder;
import family.main.project.internal.order.mapper.ItemMapper;
import family.main.project.internal.order.repository.ItemRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemService {

    ItemRepository itemRepository;
    ItemMapper itemMapper;

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Item add(ItemSaveRequest request) {

        Item item = itemMapper.toItem(request);

        return itemRepository.save(item);
    }

    public Item update(Long itemId,ItemSaveRequest request) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NO_EXISTS));

        itemMapper.updateItemFromRequest(item, request);

        return itemRepository.save(item);
    }

    public void delete(Long id) {
        if (itemRepository.existsById(id))
            throw new AppException(ErrorCode.ITEM_NO_EXISTS);

        itemRepository.deleteById(id);
    }
}