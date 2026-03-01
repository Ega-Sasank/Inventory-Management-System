package ineventory.Controller;


//import inventory.Service.Impl.ActivityLogService;
import ineventory.Entity.Transaction;
import ineventory.Entity.User;
import ineventory.Repository.TransactionRepository;
import ineventory.Repository.UserRepository;
import ineventory.Service.Impl.ActivityLogService;
import ineventory.Service.ProductService;
import ineventory.Service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ActivityLogService logService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ProductService productService;

    @Autowired
    private TransactionRepository transactionRepository;

    // ======================
    // ADMIN DASHBOARD
    // ======================
//    @GetMapping("/dashboard")
//    public String dashboard() {
//        return "admin/dashboard";
//    }

    // ======================
    // VIEW ACTIVITY LOGS
    // ======================
    @GetMapping("/logs")
    public String viewLogs(
            @RequestParam(required = false) String username,
            Model model) {

        if (username != null && !username.isEmpty()) {
            model.addAttribute("logs",
                    logService.getLogByUsername(username));
        } else {
            model.addAttribute("logs",
                    logService.getAllLogs());
        }

        return "admin/logs";
    }
    /// TO SEE NOT APPROVED USERS
    @GetMapping("/pending-users")
    public String viewPendingUsers(Model model){

        model.addAttribute("users",
                userRepository.findByStatus("PENDING"));

        return "admin/pending-users";
    }

    /// TO APPROVE USER
    @PostMapping("/approve/{id}")
    public String approveUser(@PathVariable Long id){

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus("ACTIVE");

        userRepository.save(user);

        return "redirect:/admin/pending-users";
    }

    ///  REJECT USER
    @PostMapping("/reject/{id}")
    public String rejectUser(@PathVariable Long id){

        userRepository.deleteById(id);

        return "redirect:/admin/pending-users";
    }

    /// FOR ANALYTICS AND DASHBOARDS
    @GetMapping("/dashboard")
    public String analyticsDashboard(Model model) {

        model.addAttribute("todaySales",
                transactionService.getTodaySales());

        model.addAttribute("weeklySales",
                transactionService.getWeeklySales());

        model.addAttribute("monthlySales",
                transactionService.getMonthlySales());

        model.addAttribute("totalSales",
                transactionService.getTotalSales());

        model.addAttribute("totalPurchase",
                transactionService.getTotalPurchase());

        model.addAttribute("inventoryValue",
                productService.getTotalInventoryValue());

        model.addAttribute("fastMoving",
                transactionService.getFastMovingProduct());

        model.addAttribute("slowMoving",
                transactionService.getSlowMovingProduct());

        return "admin/dashboard";
    }

}
