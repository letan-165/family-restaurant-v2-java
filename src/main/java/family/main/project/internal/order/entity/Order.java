package family.main.project.internal.order.entity;

import family.main.project.common.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @Id
    Long id;

    @Enumerated(EnumType.STRING)
    OrderStatus status;
    Integer total;
    String note;

    @Column(name = "time_booking", nullable = false)
    Date timeBooking;

    @Column(name = "time_completed", nullable = false)
    Date timeCompleted;
}