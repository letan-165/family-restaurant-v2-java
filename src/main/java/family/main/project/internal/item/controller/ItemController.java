package family.main.project.internal.item.controller;

import family.main.project.common.model.response.ApiResponse;
import family.main.project.internal.item.dto.request.ItemSaveRequest;
import family.main.project.internal.item.entity.Item;
import family.main.project.internal.item.service.ItemService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/item")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemController {
    ItemService itemService;

    @GetMapping("/public")
    ApiResponse<List<Item>> getItems(Pageable pageable){
        return ApiResponse.<List<Item>>builder()
                .message("get items")
                .result(itemService.findAll(pageable))
                .build();
    }

    @PostMapping("/public")
    ApiResponse<Item> addItem(@RequestBody ItemSaveRequest request){
        return ApiResponse.<Item>builder()
                .message("add item")
                .result(itemService.add(request))
                .build();
    }

    @PutMapping("/public/{id}")
    ApiResponse<Item> updateItem(@PathVariable Long id,@RequestBody ItemSaveRequest request){
        return ApiResponse.<Item>builder()
                .message("update item")
                .result(itemService.update(id,request))
                .build();
    }


    @DeleteMapping("/public/{id}")
    ApiResponse<Long> deleteItem(@PathVariable Long id){
        itemService.delete(id);
        return ApiResponse.<Long>builder()
                .message("delete item")
                .result(id)
                .build();
    }
}