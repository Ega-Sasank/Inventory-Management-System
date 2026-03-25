package ineventory.Service;

import ineventory.Entity.Alert;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class AlertSchedular {
    private final AlertService alertService;
    private final NotificationService notificationService;

    // runs every day at 9 AM
    //@Scheduled(cron = "0 */1 * * * ?")
    @Scheduled(cron = "0 0 17 * * ?")
    public void sendDailySummary() {

        List<Alert> alerts = alertService.getActiveAlerts();

        if (alerts.isEmpty()) {
            return;
        }

        int lowStockCount = 0;
        int outOfStockCount = 0;

        for (Alert alert : alerts) {
            if ("LOW_STOCK".equals(alert.getAlertType())) {
                lowStockCount++;
            } else if ("OUT_OF_STOCK".equals(alert.getAlertType())) {
                outOfStockCount++;
            }
        }

        StringBuilder summary = new StringBuilder();

        summary.append("INVENTORY ALERT REPORT\n\n");

        summary.append("Date: ")
                .append(java.time.LocalDate.now())
                .append("\n\n");

        summary.append("Total Active Alerts: ")
                .append(alerts.size())
                .append("\n\n");

        summary.append("LOW STOCK Alerts: ")
                .append(lowStockCount)
                .append("\n");

        summary.append("OUT OF STOCK Alerts: ")
                .append(outOfStockCount)
                .append("\n\n");

        summary.append("Alert Details:\n");

        int i = 1;

        for (Alert alert : alerts) {
            summary.append(i)
                    .append(". ")
                    .append(alert.getMessage())
                    .append("\n");
            i++;
        }

        summary.append("\nSystem: Inventory Management System");

        notificationService.sendSummary(summary.toString());

        System.out.println("Daily alert report sent.");
    }
}