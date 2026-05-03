package family.main.project.internal.cart.service;

import family.main.project.common.exception.AppException;
import family.main.project.common.exception.ErrorCode;
import family.main.project.common.model.response.ItemObjResponse;
import family.main.project.internal.cart.dto.request.CreateCartRequest;
import family.main.project.internal.cart.dto.request.UpdateQuantityRequest;
import family.main.project.internal.cart.dto.response.GetAllMyCartResponse;
import family.main.project.internal.cart.entity.CartItem;
import family.main.project.internal.cart.entity.UserCart;
import family.main.project.internal.cart.mapper.CartMapper;
import family.main.project.internal.cart.repository.CartItemRepository;
import family.main.project.internal.cart.repository.CartRepository;
import family.main.project.internal.item.entity.Item;
import family.main.project.internal.item.mapper.ItemMapper;
import family.main.project.internal.item.repository.ItemRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CartItemService {

    CartItemRepository cartItemRepository;
    CartRepository cartRepository;
    ItemRepository itemRepository;

    ItemMapper itemMapper;
    CartMapper cartMapper;

    public GetAllMyCartResponse getAllMy(String userId, Pageable pageable){
        UserCart userCart = cartRepository.findByUserId(userId)
                .orElseThrow(()->new AppException(ErrorCode.CART_NO_EXISTS));

        Long cartId = userCart.getId();

        List<CartItem> cartItems = cartItemRepository.findByCartId(cartId,pageable).getContent();

        Map<Long, CartItem> cartItemMap = cartItems.stream()
                .collect(Collectors.toMap(CartItem::getItemId, o -> o));

        List<Item> items = itemRepository.findAllById(cartItemMap.keySet());

        List<ItemObjResponse> itemObjResponses = items.stream().map(item -> {
            ItemObjResponse itemObjResponse = itemMapper.toItemResponse(item);
            CartItem cartItem = cartItemMap.get(item.getId());

            itemObjResponse = itemObjResponse.toBuilder()
                    .id(cartItem.getId())
                    .quantity(cartItem.getQuantity())
                    .objId(cartItem.getCartId())
                    .build();


            return itemObjResponse;
        }).toList();

        return GetAllMyCartResponse.builder()
                .cartId(cartId)
                .items(itemObjResponses)
                .build();
    }

    public CartItem create(CreateCartRequest request){
        if (!cartRepository.existsById(request.getCartId()))
            throw new AppException(ErrorCode.CART_NO_EXISTS);

        if (!itemRepository.existsById(request.getItemId()))
            throw new AppException(ErrorCode.ITEM_NO_EXISTS);

        CartItem cartItem = cartMapper.toOCartItem(request);

        return cartItemRepository.save(cartItem);
    }

    public CartItem updateQuantity(Long itemId, UpdateQuantityRequest request){
        CartItem cartItem = cartItemRepository.findByItemId(itemId)
                .orElseThrow(()-> new AppException(ErrorCode.ITEM_CART_NO_EXISTS));
        cartItem.setQuantity(request.getQuantity());
        return cartItemRepository.save(cartItem);
    }

    public void delete(Long id){
        if (!cartItemRepository.existsById(id))
            throw new AppException(ErrorCode.ITEM_CART_NO_EXISTS);
        cartItemRepository.deleteById(id);
    }

}
