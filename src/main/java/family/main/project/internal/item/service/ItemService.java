package family.main.project.internal.item.service;

import family.main.project.common.exception.AppException;
import family.main.project.common.exception.ErrorCode;
import family.main.project.internal.item.dto.request.ItemSaveRequest;
import family.main.project.internal.item.entity.Item;
import family.main.project.internal.item.mapper.ItemMapper;
import family.main.project.internal.item.repository.ItemRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemService {

    ItemRepository itemRepository;
    ItemMapper itemMapper;


    @Cacheable(value = "items", keyGenerator = "pageableKeyGenerator")
    public List<Item> findAll(Pageable pageable) {
        return itemRepository.findAll(pageable).getContent();
    }

    @CacheEvict(value = {"items"}, allEntries = true)
    public Item add(ItemSaveRequest request) {

        Item item = itemMapper.toItem(request);

        return itemRepository.save(item);
    }

    @CacheEvict(value = {"items"}, allEntries = true)
    public Item update(Long itemId,ItemSaveRequest request) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NO_EXISTS));

        itemMapper.updateItemFromRequest(item, request);

        return itemRepository.save(item);
    }

    @CacheEvict(value = {"items"}, allEntries = true)
    public void delete(Long id) {
        if (!itemRepository.existsById(id))
            throw new AppException(ErrorCode.ITEM_NO_EXISTS);

        itemRepository.deleteById(id);
    }
}