package ineventory.Service;

import ineventory.Entity.Product;
import ineventory.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


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
        alertService.evaluateStockAlert(product);

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
        alertService.evaluateStockAlert(product);

        return "Stock updated successfully";
    }

    public List<Product> getAllProducts() {
        return productRepository.findByStatus("ACTIVE");
    }

    public List<Product> getLowStockProducts() {
        return productRepository.findAll()
                .stream()
                .filter(p -> p.getStockQuantity() <= p.getMinStockLevel())
                .toList();
    }

    /// for inventory total value to present in dashboard
    public Double getTotalInventoryValue() {
        return productRepository.findByStatus("ACTIVE")
                .stream()
                .mapToDouble(p -> p.getStockQuantity() * p.getUnitPrice())
                .sum();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public String updateProduct(Long id, Product updatedProduct) {

        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isEmpty()) {
            return "Product not found";
        }

        Product existing = optionalProduct.get();

        // Validation
        if (updatedProduct.getUnitPrice() == null ||
                updatedProduct.getUnitPrice() <= 0) {
            return "Invalid Unit Price";
        }

        if (updatedProduct.getMinStockLevel() == null ||
                updatedProduct.getMinStockLevel() < 0) {
            return "Invalid Minimum Stock Level";
        }

        // Set updated values
        existing.setName(updatedProduct.getName());
        existing.setCategory(updatedProduct.getCategory());
        existing.setSupplier(updatedProduct.getSupplier());
        existing.setUnitPrice(updatedProduct.getUnitPrice());
        existing.setMinStockLevel(updatedProduct.getMinStockLevel());

        productRepository.save(existing);

        return "SUCCESS";
    }

    // =========================
    //  SOFT DELETE
    // =========================
    public String deleteProduct(Long id) {

        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isEmpty()) {
            return "Product not found";
        }

        Product product = optionalProduct.get();

        if ("INACTIVE".equals(product.getStatus())) {
            return "Already deleted";
        }

        product.setStatus("INACTIVE");
        product.setStockQuantity(0);

        productRepository.save(product);

        return "SUCCESS";
    }

    ///  to restore the soft deleted products

    public String restoreProduct(Long id, int quantity) {

        Optional<Product> optionalProduct = productRepository.findById(id);

        if(optionalProduct.isEmpty()){
            return "Product not found";
        }

        Product product = optionalProduct.get();

        if("ACTIVE".equals(product.getStatus())){
            return "Product already active";
        }

        product.setStatus("ACTIVE");
        product.setStockQuantity(quantity);   //  VERY IMPORTANT

        productRepository.save(product);

        return "Product restored successfully";
    }


    public List<Product> getDeletedProducts(){
        return productRepository.findByStatus("INACTIVE");
    }

}
