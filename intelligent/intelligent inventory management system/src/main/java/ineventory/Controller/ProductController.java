package ineventory.Controller;

import ineventory.Entity.Product;
import ineventory.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // =========================
    // 🔵 ADMIN DASHBOARD
    // =========================
    /*@GetMapping("/admin")
    public String showAdminPage(Model model) {

        model.addAttribute("product", new Product());
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("lowStockProducts",
                productService.getLowStockProducts());

        return "admin/dashboard";
    }*/

    // 🔵 ADMIN - Add Product
    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product product,
                             RedirectAttributes redirectAttributes) {

        String result = productService.addProduct(product);
        redirectAttributes.addFlashAttribute("message", result);

        return "redirect:/products/admin/products";
    }


    // =========================
    // 🟢 STAFF DASHBOARD
    // =========================
    /*@GetMapping("/employee")
    public String showStaffPage(Model model) {

        model.addAttribute("products",
                productService.getAllProducts());

        model.addAttribute("lowStockProducts",
                productService.getLowStockProducts());

        return "employee/dashboard";  // your green UI page
    }*/

    // 🟢 STOCK IN (Admin + Staff)
    @PostMapping("/stock-in")
    public String stockIn(@RequestParam Long id,
                          @RequestParam int qty,
                          RedirectAttributes redirectAttributes) {

        String result = productService.stockIn(id, qty);
        redirectAttributes.addFlashAttribute("message", result);

        //return "redirect:/products/employee";
        return "redirect:/products/employee/products";
    }

    // 🟢 STOCK OUT (Admin + Staff)
    @PostMapping("/stock-out")
    public String stockOut(@RequestParam Long id,
                           @RequestParam int qty,
                           RedirectAttributes redirectAttributes) {

        String result = productService.stockOut(id, qty);
        redirectAttributes.addFlashAttribute("message", result);

        //return "redirect:/products/employee";
        return "redirect:/products/employee/products";

    }

    @GetMapping("/admin/products")
    public String adminProducts(Model model) {
        model.addAttribute("product",new Product());
        model.addAttribute("products", productService.getAllProducts());
        return "admin/products";
    }

    @GetMapping("/employee/products")
    public String employeeProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "employee/products";
    }

}
