package ineventory.Service;

import ineventory.Entity.Product;
import org.springframework.stereotype.Service;

@Service
public class AlertService {
    public void checkLowStock(Product product) {
        if (product.getStockQuantity() <= product.getMinStockLevel()) {
            System.out.println("⚠ LOW STOCK ALERT for Product: " + product.getName());
        }
    }
}
