package ineventory.Controller;


//import inventory.Service.Impl.ActivityLogService;
import ineventory.Entity.User;
import ineventory.Repository.UserRepository;
import ineventory.Service.Impl.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ActivityLogService logService;

    @Autowired
    private UserRepository userRepository;

    // ======================
    // ADMIN DASHBOARD
    // ======================
    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

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
}
