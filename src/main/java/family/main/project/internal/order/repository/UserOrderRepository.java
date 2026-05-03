package family.main.project.internal.order.repository;

import family.main.project.common.enums.OrderStatus;
import family.main.project.internal.order.entity.UserOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserOrderRepository extends JpaRepository<UserOrder, Long> {
    Optional<UserOrder> findByOrderId(Long orderId);

    @Query(value = """
    SELECT uo.*
    FROM user_order uo
    JOIN orders o ON uo.order_id = o.id
    WHERE uo.user_id = :userId
      AND o.status = :status
""",
            countQuery = """
    SELECT COUNT(*)
    FROM user_order uo
    JOIN orders o ON uo.order_id = o.id
    WHERE uo.user_id = :userId
      AND o.status = :status
""",
            nativeQuery = true)
    Page<UserOrder> findByUserIdAndOrderStatus(
            @Param("userId") String userId,
            @Param("status") String status,
            Pageable pageable
    );
}
