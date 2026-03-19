package family.main.project.internal.order.repository;

import family.main.project.internal.order.entity.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserOrderRepository extends JpaRepository<UserOrder, Long> {
    Optional<UserOrder> findByOrderId(Long orderId);
    List<UserOrder> findAllByUserId(String userId);
}
