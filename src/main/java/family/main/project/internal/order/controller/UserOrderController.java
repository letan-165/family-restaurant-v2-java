package family.main.project.internal.order.controller;

import family.main.project.common.enums.OrderStatus;
import family.main.project.common.model.response.ApiResponse;
import family.main.project.internal.order.dto.request.OrderCreateRequest;
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
@RequestMapping("/user/order")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserOrderController {
    UserOrderService userOrderService;

    @GetMapping("/public")
    ApiResponse<GetAllMyResponse> getAllMy(@RequestParam OrderStatus status, Pageable pageable){
        String userId = AuthService.getUserIdFromToken();

        return ApiResponse.<GetAllMyResponse>builder()
                .message("get all my order")
                .result(userOrderService.getAllMy(userId,status,pageable))
                .build();
    }

    @PutMapping("/public/{orderId}")
    ApiResponse<OrderUpdateInfoResponse> updateInfo(@PathVariable Long orderId, @RequestBody OrderUpdateInfoRequest request){
        return ApiResponse.<OrderUpdateInfoResponse>builder()
                .message("update info order")
                .result(userOrderService.updateInfo(orderId ,request))
                .build();
    }

}
