package ineventory.Service;

import ineventory.Entity.Transaction;
import ineventory.Repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionFilteringService {

    private final TransactionRepository transactionRepository;

    public List<Transaction> getFilteredTransactions(
            Long productId,
            String type,
            LocalDate startDate,
            LocalDate endDate,
            String user,
            Integer minQuantity
    ){

        List<Transaction> transactions =
                transactionRepository.findAll();

        if(productId != null){
            transactions = transactions.stream()
                    .filter(t -> t.getProduct().getProductId()
                            .equals(productId))
                    .collect(Collectors.toList());
        }

        if(type != null){
            transactions = transactions.stream()
                    .filter(t -> t.getType().equals(type))
                    .collect(Collectors.toList());
        }

        if(startDate != null && endDate != null){
            transactions = transactions.stream()
                    .filter(t -> !t.getTimestamp().toLocalDate()
                            .isBefore(startDate))
                    .filter(t -> !t.getTimestamp().toLocalDate()
                            .isAfter(endDate))
                    .collect(Collectors.toList());
        }

        if(user != null){
            transactions = transactions.stream()
                    .filter(t -> user.equals(t.getPerformedByUser()))
                    .collect(Collectors.toList());
        }

        if(minQuantity != null){
            transactions = transactions.stream()
                    .filter(t -> t.getQuantity() >= minQuantity)
                    .collect(Collectors.toList());
        }

        return transactions;
    }
}