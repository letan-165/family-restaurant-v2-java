package family.main.project.internal.cart.controller;

import family.main.project.common.model.response.ApiResponse;
import family.main.project.internal.cart.dto.request.CreateCartRequest;
import family.main.project.internal.cart.dto.request.UpdateQuantityRequest;
import family.main.project.internal.cart.dto.response.GetAllMyCartResponse;
import family.main.project.internal.cart.entity.CartItem;
import family.main.project.internal.cart.service.CartItemService;
import family.main.project.internal.order.dto.request.OrderUpdateInfoRequest;
import family.main.project.internal.order.dto.response.GetAllMyResponse;
import family.main.project.internal.order.dto.response.OrderUpdateInfoResponse;
import family.main.project.internal.order.service.UserOrderService;
import family.main.project.internal.user.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart/item")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartItemController {
    CartItemService cartItemService;

    @GetMapping("/public")
    ApiResponse<GetAllMyCartResponse> getAllMy(Pageable pageable){
        String userId = AuthService.getUserIdFromToken();

        return ApiResponse.<GetAllMyCartResponse>builder()
                .message("get all my cart")
                .result(cartItemService.getAllMy(userId,pageable))
                .build();
    }

    @PostMapping("/public")
    ApiResponse<CartItem> create(@RequestBody CreateCartRequest request){
        return ApiResponse.<CartItem>builder()
                .message("create cart")
                .result(cartItemService.create(request))
                .build();
    }

    @PutMapping("/public/{itemId}")
    ApiResponse<CartItem> updateQuantity(@PathVariable Long itemId, @RequestBody UpdateQuantityRequest request){
        return ApiResponse.<CartItem>builder()
                .message("update quantity item cart")
                .result(cartItemService.updateQuantity(itemId ,request))
                .build();
    }

    @DeleteMapping("/public/{id}")
    ApiResponse<Long> detele(@PathVariable Long id){
        cartItemService.delete(id);
        return ApiResponse.<Long>builder()
                .message("delete item cart")
                .result(id)
                .build();
    }

}
