package ineventory.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(unique = true, nullable = false)
    private String sku;

    @Column(nullable = false)
    private String name;

    private String category;

    private String supplier;

    private Double unitPrice;

    private Integer stockQuantity;

    private Integer minStockLevel;

    private String status; // ACTIVE / INACTIVE

    @OneToMany(mappedBy = "product")
    private List<Transaction> transactions;
}
