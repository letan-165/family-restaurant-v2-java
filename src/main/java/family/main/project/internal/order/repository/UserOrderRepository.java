package family.main.project.internal.order.repository;

import family.main.project.internal.order.entity.UserOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserOrderRepository extends JpaRepository<UserOrder, Long> {
    Optional<UserOrder> findByOrderId(Long orderId);
    Page<UserOrder> findAllByUserId(String userId, Pageable pageable);
}
