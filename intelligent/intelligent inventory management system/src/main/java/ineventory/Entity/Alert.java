package ineventory.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alertId;

    private Long productId;

    private String alertType;   // LOW_STOCK, OUT_OF_STOCK

    private String severity;    // HIGH, MEDIUM, LOW

    private String message;

    private LocalDateTime createdDate;

    private String status;      // ACTIVE, RESOLVED

}
