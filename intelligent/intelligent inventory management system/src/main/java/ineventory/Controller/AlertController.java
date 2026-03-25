package ineventory.Controller;


import ineventory.Entity.Alert;
import ineventory.Service.AlertFilteringService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertFilteringService alertFilteringService;

    // ADMIN ALERT PAGE
    @GetMapping("/admin")
    public String adminAlerts(Model model) {

        List<Alert> alerts =
                alertFilteringService.getFilteredAlerts(
                        null,null,null,"ACTIVE");// if we put null in place of active  it will shows resolved errors also

        model.addAttribute("alerts", alerts);

        return "admin/alerts";
    }

    // EMPLOYEE ALERT PAGE
    @GetMapping("/employee")
    public String employeeAlerts(Model model){

        List<Alert> alerts =
                alertFilteringService.getFilteredAlerts(
                        null,null,null,"ACTIVE");

        model.addAttribute("alerts",alerts);

        return "employee/alerts";
    }
}
