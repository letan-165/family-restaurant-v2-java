package family.main.project.internal.cart.repository;

import family.main.project.internal.cart.entity.CartItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCartId(Long cartId, Pageable pageable);
    long countByCartIdAndItemIdIn(Long cartId, List<Long> itemIds);
    void deleteAllByItemIdIn(List<Long> itemIds);
}
