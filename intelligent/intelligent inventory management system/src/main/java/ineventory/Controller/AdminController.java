package ineventory.Controller;


//import inventory.Service.Impl.ActivityLogService;
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
}
