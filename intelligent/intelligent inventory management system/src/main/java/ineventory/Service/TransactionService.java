package ineventory.Service;


import ineventory.Entity.Product;
import ineventory.Entity.Transaction;
import ineventory.Repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public void log(String type, Product product, int quantity) {

        Transaction transaction = new Transaction();
        transaction.setType(type);
        transaction.setProduct(product);
        transaction.setQuantity(quantity);
        transaction.setTimestamp(LocalDateTime.now());

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
