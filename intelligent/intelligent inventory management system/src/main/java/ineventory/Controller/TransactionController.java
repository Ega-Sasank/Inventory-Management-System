package ineventory.Controller;

import ineventory.Entity.Transaction;
import ineventory.Repository.TransactionRepository;
import ineventory.Service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

//    @GetMapping("/admin")
//    public String adminTransactions(Model model) {
//        model.addAttribute("transactions",transactionService.getAllTransactions());
//        return "admin/transactions";
//    }
//
//    @GetMapping("/employee")
//    public String employeeTransactions(Model model) {
//        model.addAttribute("transactions",transactionService.getAllTransactions());
//        return "employee/transactions";
//    }

    // 🔵 ADMIN SIDE
    @GetMapping("/admin")
    public String adminTransactions(
            @RequestParam(required = false) Long productId,
            Model model) {

        List<Transaction> transactions;

        if (productId != null) {
            transactions = transactionService
                    .getTransactionsByProductId(productId);
        } else {
            transactions = transactionService.getAllTransactions();
        }

        model.addAttribute("transactions", transactions);
        return "admin/transactions";
    }

    // 🟢 EMPLOYEE SIDE
    @GetMapping("/employee")
    public String employeeTransactions(
            @RequestParam(required = false) Long productId,
            Model model) {

        List<Transaction> transactions;

        if (productId != null) {
            transactions = transactionService
                    .getTransactionsByProductId(productId);
        } else {
            transactions = transactionService.getAllTransactions();
        }

        model.addAttribute("transactions", transactions);
        return "employee/transactions";
    }
}