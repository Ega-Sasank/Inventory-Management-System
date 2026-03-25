package ineventory.Service;

import ineventory.Entity.Product;
import org.springframework.stereotype.Service;

@Service
public class AlertValidationService {
    public String validateProductForAlert(Product product){

        if(product == null)
            return "Invalid product";

        if(!"ACTIVE".equals(product.getStatus()))
            return "Inactive product";

        if(product.getMinStockLevel() < 0)
            return "Invalid stock threshold";

        return "VALID";
    }
}
