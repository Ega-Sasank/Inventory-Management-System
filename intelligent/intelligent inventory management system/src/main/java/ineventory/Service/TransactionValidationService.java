package ineventory.Service;

import org.springframework.stereotype.Service;

@Service
public class TransactionValidationService {

    public String validateTransaction(
            String type,
            Long productId,
            int quantity,
            String user
    ){

        if(productId == null){
            return "Invalid product";
        }

        if(quantity <= 0){
            return "Invalid quantity";
        }

        if(user == null || user.isEmpty()){
            return "Unauthorized action";
        }

        if(!type.equals("STOCK_IN") && !type.equals("STOCK_OUT")){
            return "Invalid transaction type";
        }

        return "VALID";
    }

}
