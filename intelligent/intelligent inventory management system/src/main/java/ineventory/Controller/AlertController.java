package ineventory.Controller;

import ineventory.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/alerts")
public class AlertController {

    @Autowired
    private ProductService productService;


    @GetMapping("/admin")
    public String adminAlerts(Model model) {
        model.addAttribute("lowStockProducts",
                productService.getLowStockProducts());
        return "admin/alerts";
    }

    @GetMapping("/employee")
    public String employeeAlerts(Model model) {
        model.addAttribute("lowStockProducts",
                productService.getLowStockProducts());
        return "employee/alerts";
    }
}
