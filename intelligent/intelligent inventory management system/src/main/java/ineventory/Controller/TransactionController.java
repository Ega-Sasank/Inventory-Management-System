package ineventory.Controller;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import ineventory.Entity.Transaction;
import ineventory.Repository.TransactionRepository;
import ineventory.Service.TransactionService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    //  ADMIN SIDE
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

    /// PDF TO DOWNLOAD TRANSACTIONS REPORT for all as well as by product id
    /// ADMIN SIDE
    @GetMapping("/admin/report")
    public void downloadTransactionsPdf(
            @RequestParam(required = false) Long productId,
            HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=transactions-report.pdf");

        List<Transaction> transactions;

        if (productId != null) {
            transactions = transactionService.getTransactionsByProductId(productId);
        } else {
            transactions = transactionService.getAllTransactions();
        }

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        if (productId != null) {
            document.add(new Paragraph("Transaction Report - Product ID: " + productId));
        } else {
            document.add(new Paragraph("Transaction Report - All"));
        }

        document.add(new Paragraph(" "));

        Table table = new Table(6);

        table.addCell("ID");
        table.addCell("Type");
        table.addCell("Quantity");
        table.addCell("Time");
        table.addCell("Product ID");
        table.addCell("Product Name");

        for (Transaction t : transactions) {
            table.addCell(String.valueOf(t.getId()));
            table.addCell(t.getType());
            table.addCell(String.valueOf(t.getQuantity()));
            table.addCell(t.getTimestamp().toString());
            table.addCell(String.valueOf(t.getProduct().getProductId()));
            table.addCell(t.getProduct().getName());
        }

        document.add(table);
        document.close();
    }

    /// Employee side
    @GetMapping("/employee/report")
    public void downloadTransactionsPdfE(
            @RequestParam(required = false) Long productId,
            HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=transactions-report.pdf");

        List<Transaction> transactions;

        if (productId != null) {
            transactions = transactionService.getTransactionsByProductId(productId);
        } else {
            transactions = transactionService.getAllTransactions();
        }

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        if (productId != null) {
            document.add(new Paragraph("Transaction Report - Product ID: " + productId));
        } else {
            document.add(new Paragraph("Transaction Report - All"));
        }

        document.add(new Paragraph(" "));

        Table table = new Table(6);

        table.addCell("ID");
        table.addCell("Type");
        table.addCell("Quantity");
        table.addCell("Time");
        table.addCell("Product ID");
        table.addCell("Product Name");

        for (Transaction t : transactions) {
            table.addCell(String.valueOf(t.getId()));
            table.addCell(t.getType());
            table.addCell(String.valueOf(t.getQuantity()));
            table.addCell(t.getTimestamp().toString());
            table.addCell(String.valueOf(t.getProduct().getProductId()));
            table.addCell(t.getProduct().getName());
        }

        document.add(table);
        document.close();
    }

}