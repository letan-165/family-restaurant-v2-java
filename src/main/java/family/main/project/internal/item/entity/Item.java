package family.main.project.internal.item.entity;

import family.main.project.common.enums.ItemStatus;
import family.main.project.common.enums.ItemType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "items")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;

    @Enumerated(EnumType.STRING)
    ItemStatus status;

    @Enumerated(EnumType.STRING)
    ItemType type;

    int sold;
    Integer price;
    String picture;
    String description;
}
