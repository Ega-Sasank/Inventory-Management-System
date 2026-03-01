package ineventory.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;//stock in /stock out

    private int quantity;
    private LocalDateTime timestamp;

    /// many to one relationship
    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;

}
