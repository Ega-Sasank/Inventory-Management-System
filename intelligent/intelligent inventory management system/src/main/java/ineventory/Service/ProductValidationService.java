package ineventory.Service;

import ineventory.Entity.Product;
import ineventory.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductValidationService {

    @Autowired
    private ProductRepository productRepository;

    public String validateNewProduct(Product product) {

        if (product.getSku() == null || product.getSku().isEmpty())
            return "SKU is mandatory";

        if (productRepository.findBySku(product.getSku()).isPresent())
            return "Duplicate SKU";

        if (product.getName() == null || product.getName().isEmpty())
            return "Product name required";

        if (product.getUnitPrice() <= 0)
            return "Invalid price";

        if (product.getMinStockLevel() < 0)
            return "Invalid minimum stock level";

        if (product.getStockQuantity() < 0)
            return "Invalid initial stock";

        return "VALID";
    }

    public String validateStockIn(Product product, int quantity) {

        if (product == null)
            return "Product not found";

        if (!"ACTIVE".equals(product.getStatus()))
            return "Inactive product";

        if (quantity <= 0)
            return "Invalid quantity";

        return "VALID";
    }

    public String validateStockOut(Product product, int quantity) {

        if (product == null)
            return "Product not found";

        if (!"ACTIVE".equals(product.getStatus()))
            return "Inactive product";

        if (quantity <= 0)
            return "Invalid quantity";

        if (product.getStockQuantity() < quantity)
            return "Insufficient stock";

        return "VALID";
    }
}
