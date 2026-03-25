package ineventory.Service;


import ineventory.Entity.Product;
import ineventory.Entity.Transaction;
import ineventory.Repository.ProductRepository;
import ineventory.Repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TransactionValidationService validationService;

    public void log(String type, Product product, int quantity){

        String user =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        String validation =
                validationService.validateTransaction(
                        type,
                        product.getProductId(),
                        quantity,
                        user
                );

        if(!validation.equals("VALID")){
            return;
        }

        Transaction transaction = new Transaction();

        transaction.setProduct(product);
        transaction.setQuantity(quantity);
        transaction.setType(type);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setPerformedByUser(user);

        transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();

    }

    public List<Transaction> getTransactionsByProductId(Long productId) {
        return transactionRepository.findByProduct_ProductId(productId);
    }

    ///  to find fast and slow moving products
    public Map<Product, Integer> getSalesCountPerProduct() {

        List<Transaction> transactions = transactionRepository.findAll();

        Map<Product, Integer> salesMap = new HashMap<>();

        for (Transaction t : transactions) {
            if (t.getType().equals("STOCK_OUT")) {
                Product product = t.getProduct();
                salesMap.put(product,
                        salesMap.getOrDefault(product, 0) + t.getQuantity());
            }
        }

        return salesMap;
    }

//    public Map<String, Integer> getTopSellingProducts() {
//
//        Map<Product, Integer> salesMap = getSalesCountPerProduct();
//
//        return salesMap.entrySet()
//                .stream()
//                .sorted((a,b) -> b.getValue().compareTo(a.getValue()))
//                .limit(5)
//                .collect(
//                        java.util.stream.Collectors.toMap(
//                                e -> e.getKey().getName(),
//                                e -> e.getValue(),
//                                (a,b) -> a,
//                                java.util.LinkedHashMap::new
//                        )
//                );
//    }

    public Product getFastMovingProduct() {
        return getSalesCountPerProduct()
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public Product getSlowMovingProduct() {
        return getSalesCountPerProduct()
                .entrySet()
                .stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    ///  summary based on daily weekly monthly
    public long getTodaySales(List<Transaction> transactions) {
        return transactions
                .stream()
                .filter(t -> t.getType().equals("STOCK_OUT"))
                .filter(t -> t.getTimestamp().toLocalDate()
                        .equals(LocalDate.now()))
                .count();
    }
    public long getWeeklySales(List<Transaction> transactions) {
        return transactions
                .stream()
                .filter(t -> t.getType().equals("STOCK_OUT"))
                .filter(t -> t.getTimestamp().toLocalDate()
                        .isAfter(LocalDate.now().minusDays(7)))
                .count();
    }

    public long getMonthlySales(List<Transaction> transactions) {
        return transactions
                .stream()
                .filter(t -> t.getType().equals("STOCK_OUT"))
                .filter(t -> t.getTimestamp().toLocalDate()
                        .isAfter(LocalDate.now().minusMonths(1)))
                .count();
    }

    ///  sales vs purchase trend counting stock_in and stock-out
    public long getTotalSales() {
        return transactionRepository.findAll()
                .stream()
                .filter(t -> t.getType().equals("STOCK_OUT"))
                .count();
    }

    public long getTotalPurchase() {
        return transactionRepository.findAll()
                .stream()
                .filter(t -> t.getType().equals("STOCK_IN"))
                .count();
    }
}
