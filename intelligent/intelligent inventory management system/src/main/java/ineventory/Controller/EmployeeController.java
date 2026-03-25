package ineventory.Controller;

import ineventory.Service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private AlertService alertService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("alertCount", alertService.getActiveAlertCount());

        return "employee/dashboard";
    }
}
