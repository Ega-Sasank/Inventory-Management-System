package ineventory.Controller;


//import inventory.Service.Impl.ActivityLogService;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import ineventory.Entity.Product;
import ineventory.Entity.Transaction;
import ineventory.Entity.User;
import ineventory.Repository.TransactionRepository;
import ineventory.Repository.UserRepository;
import ineventory.Service.Impl.ActivityLogService;
import ineventory.Service.ProductService;
import ineventory.Service.TransactionService;
import jakarta.servlet.http.HttpServletResponse;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
//import java.awt.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
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
        List<Transaction> transactions = transactionService.getAllTransactions();

        model.addAttribute("todaySales",
                transactionService.getTodaySales(transactions));

        model.addAttribute("weeklySales",
                transactionService.getWeeklySales(transactions));

        model.addAttribute("monthlySales",
                transactionService.getMonthlySales(transactions));

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

        // 🔹 ADD THESE 2 LINES
        model.addAttribute("products",
                productService.getAllProducts());

        model.addAttribute("lowStockProducts",
                productService.getLowStockProducts());

        return "admin/dashboard";
    }



    /// PDF method to download reports
//    @GetMapping("/dashboard/pdf")
//    public void exportDashboardPdf(HttpServletResponse response) throws Exception {
//
//        response.setContentType("application/pdf");
//        response.setHeader("Content-Disposition",
//                "attachment; filename=dashboard-report.pdf");
//
//        PdfWriter writer = new PdfWriter(response.getOutputStream());
//        PdfDocument pdf = new PdfDocument(writer);
//        Document document = new Document(pdf);
//
//        // Title
//        document.add(new Paragraph("Inventory Dashboard Report")
//                .setBold()
//                .setFontSize(18));
//
//        document.add(new Paragraph(" "));
//
//        // KPI Section
//        document.add(new Paragraph("Today Sales: " +
//                transactionService.getTodaySales()));
//
//        document.add(new Paragraph("Weekly Sales: " +
//                transactionService.getWeeklySales()));
//
//        document.add(new Paragraph("Monthly Sales: " +
//                transactionService.getMonthlySales()));
//
//        document.add(new Paragraph("Total Inventory Value: ₹ " +
//                productService.getTotalInventoryValue()));
//
//        document.add(new Paragraph(" "));
//
//        // Stock Table
//        document.add(new Paragraph("Stock Report").setBold());
//
//        Table table = new Table(4);
//        table.addCell("ID");
//        table.addCell("Name");
//        table.addCell("Stock");
//        table.addCell("Min Level");
//
//        for (Product p : productService.getAllProducts()) {
//            table.addCell(String.valueOf(p.getProductId()));
//            table.addCell(p.getName());
//            table.addCell(String.valueOf(p.getStockQuantity()));
//            table.addCell(String.valueOf(p.getMinStockLevel()));
//        }
//
//        document.add(table);
//
//        document.add(new Paragraph(" "));
//
//        // Low Stock Section
//        document.add(new Paragraph("Low Stock Report").setBold());
//
//        Table lowTable = new Table(3);
//        lowTable.addCell("Name");
//        lowTable.addCell("Stock");
//        lowTable.addCell("Min Level");
//
//        for (Product p : productService.getLowStockProducts()) {
//            lowTable.addCell(p.getName());
//            lowTable.addCell(String.valueOf(p.getStockQuantity()));
//            lowTable.addCell(String.valueOf(p.getMinStockLevel()));
//        }
//
//        document.add(lowTable);
//
//        document.close();
//    }

    /// PDF OF STOCK REPORT
    @GetMapping("/dashboard/stock-pdf")
    public void exportStockPdf(HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=stock-report.pdf");

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Stock Report")
                .setBold()
                .setFontSize(18));

        document.add(new Paragraph(" "));

        Table table = new Table(6);

        table.addCell("ID");
        table.addCell("Name");
        table.addCell("Stock");
        table.addCell("Min Level");
        table.addCell("Unit Price");
        table.addCell("Total Value");

        for (Product p : productService.getAllProducts()) {
            table.addCell(String.valueOf(p.getProductId()));
            table.addCell(p.getName());
            table.addCell(String.valueOf(p.getStockQuantity()));
            table.addCell(String.valueOf(p.getMinStockLevel()));
            table.addCell(String.valueOf(p.getUnitPrice()));
            table.addCell(String.valueOf(
                    p.getStockQuantity() * p.getUnitPrice()));
        }

        document.add(table);
        document.close();
    }

    /// PDF OF LOW STOCK REPORT
    @GetMapping("/dashboard/lowstock-pdf")
    public void exportLowStockPdf(HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=low-stock-report.pdf");

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Low Stock Report")
                .setBold()
                .setFontSize(18));

        document.add(new Paragraph(" "));

        Table table = new Table(3);

        table.addCell("Name");
        table.addCell("Stock");
        table.addCell("Min Level");

        for (Product p : productService.getLowStockProducts()) {
            table.addCell(p.getName());
            table.addCell(String.valueOf(p.getStockQuantity()));
            table.addCell(String.valueOf(p.getMinStockLevel()));
        }

        document.add(table);
        document.close();
    }

    /// SALES PURCHASE REPORT AS BARGRAPH PDF
    @GetMapping("/dashboard/sales-purchase-pdf")
    public void exportSalesPurchasePdf(HttpServletResponse response) throws Exception {

        long sales = transactionService.getTotalSales();
        long purchase = transactionService.getTotalPurchase();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=sales-vs-purchase-report.pdf");

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Sales vs Purchase Report")
                .setBold()
                .setFontSize(18));

        document.add(new Paragraph(" "));

        // Create dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(sales, "Stock", "Sales");
        dataset.addValue(purchase, "Stock", "Purchase");

        // Create chart
        JFreeChart chart = ChartFactory.createBarChart(
                "Sales vs Purchase",
                "Type",
                "Count",
                dataset
        );

        chart.setBackgroundPaint(Color.white);

        // Convert chart to image
        BufferedImage bufferedImage =
                chart.createBufferedImage(500, 400);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);

        ImageData imageData =
                ImageDataFactory.create(baos.toByteArray());

        Image image = new Image(imageData);

        document.add(image);

        document.close();
    }
}
