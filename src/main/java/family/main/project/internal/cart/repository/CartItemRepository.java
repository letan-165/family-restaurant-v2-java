package family.main.project.internal.cart.repository;

import family.main.project.internal.cart.entity.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Page<CartItem> findByCartId(Long cartId, Pageable pageable);
    Optional<CartItem> findByItemId(Long itemId);
}
