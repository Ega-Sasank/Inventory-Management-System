package ineventory.Service;

import ineventory.Entity.Product;
import ineventory.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductValidationService validationService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AlertService alertService;


    /// ADMIN ONLY (call from Admin controller)
    public String addProduct(Product product) {

        String validation = validationService.validateNewProduct(product);
        if (!validation.equals("VALID"))
            return validation;

        product.setStatus("ACTIVE");
        productRepository.save(product);

        return "Product added successfully";
    }


    /// Used by Admin + Staff
    public String stockIn(Long id, int qty) {

        Product product = productRepository.findById(id).orElse(null);
        String validation = validationService.validateStockIn(product, qty);

        if (!validation.equals("VALID"))
            return validation;

        product.setStockQuantity(product.getStockQuantity() + qty);
        productRepository.save(product);

        transactionService.log("STOCK_IN",product, qty);
        alertService.checkLowStock(product);

        return "Stock updated successfully";
    }

    public String stockOut(Long id, int qty) {

        Product product = productRepository.findById(id).orElse(null);
        String validation = validationService.validateStockOut(product, qty);

        if (!validation.equals("VALID"))
            return validation;

        product.setStockQuantity(product.getStockQuantity() - qty);
        productRepository.save(product);

        transactionService.log("STOCK_OUT", product, qty);
        alertService.checkLowStock(product);

        return "Stock updated successfully";
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getLowStockProducts() {
        return productRepository.findAll()
                .stream()
                .filter(p -> p.getStockQuantity() <= p.getMinStockLevel())
                .toList();
    }

}
