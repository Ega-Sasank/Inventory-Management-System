package ineventory.Service;



import ineventory.Entity.Alert;
import ineventory.Entity.Product;
import ineventory.Repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final AlertValidationService validationService;
    private final NotificationService notificationService;

    public void evaluateStockAlert(Product product){

        String validation =
                validationService.validateProductForAlert(product);

        if(!validation.equals("VALID"))
            return;

        if(product.getStockQuantity() <= 0){

            createAlert(product,"OUT_OF_STOCK","HIGH");

        }
        else if(product.getStockQuantity() <
                product.getMinStockLevel()){

            createAlert(product,"LOW_STOCK","MEDIUM");

        }
        else{

            // STOCK BACK TO NORMAL
            resolveAlert(product,"LOW_STOCK");
            resolveAlert(product,"OUT_OF_STOCK");
        }
    }

    private void createAlert(
            Product product,
            String type,
            String severity
    ){

        boolean exists =
                alertRepository.existsByProductIdAndAlertTypeAndStatus(
                        product.getProductId(),
                        type,
                        "ACTIVE"
                );

        if(exists)
            return;

        Alert alert = Alert.builder()
                .productId(product.getProductId())
                .alertType(type)
                .severity(severity)
                .message(generateMessage(product,type))
                .createdDate(LocalDateTime.now())
                .status("ACTIVE")
                .build();

        alertRepository.save(alert);

        notificationService.sendAlert(alert);
    }

    private String generateMessage(Product product,String type){

        if(type.equals("OUT_OF_STOCK"))
            return "Product "+product.getName()+" is OUT OF STOCK";

        return "Product "+product.getName()+" is LOW on stock";
    }
    public long getActiveAlertCount(){
        return alertRepository.findAll()
                .stream()
                .filter(a -> "ACTIVE".equals(a.getStatus()))
                .count();
    }

    private void resolveAlert(Product product, String type){

        Optional<Alert> alert =
                alertRepository.findByProductIdAndAlertTypeAndStatus(
                        product.getProductId(),
                        type,
                        "ACTIVE"
                );

        if(alert.isPresent()){

            Alert a = alert.get();

            a.setStatus("RESOLVED");

            alertRepository.save(a);
        }
    }

    public List<Alert> getActiveAlerts(){

        return alertRepository.findAll()
                .stream()
                .filter(a -> "ACTIVE".equals(a.getStatus()))
                .toList();
    }


}