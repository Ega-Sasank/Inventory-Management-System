package ineventory.Repository;

import ineventory.Entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlertRepository extends JpaRepository<Alert,Long> {

    boolean existsByProductIdAndAlertTypeAndStatus(Long productId, String alertType,String status);

    Optional<Alert> findByProductIdAndAlertTypeAndStatus(
            Long productId,
            String alertType,
            String status
    );
}
