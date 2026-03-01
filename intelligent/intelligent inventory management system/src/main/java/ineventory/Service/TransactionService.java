package ineventory.Service;


import ineventory.Entity.Product;
import ineventory.Entity.Transaction;
import ineventory.Repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
}
