package ineventory.Service;

import ineventory.Entity.Alert;
import ineventory.Repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertFilteringService {

    private final AlertRepository alertRepository;

    public List<Alert> getFilteredAlerts(
            String type,
            String severity,
            Long productId,
            String status
    ){

        ///List<Alert> alerts = alertRepository.findAll();
        List<Alert> alerts =
                alertRepository.findAll()
                        .stream()
                        .sorted((a,b)->b.getCreatedDate().compareTo(a.getCreatedDate()))
                        .toList();

        if(type != null)
            alerts = alerts.stream()
                    .filter(a -> a.getAlertType().equals(type))
                    .collect(Collectors.toList());

        if(severity != null)
            alerts = alerts.stream()
                    .filter(a -> a.getSeverity().equals(severity))
                    .collect(Collectors.toList());

        if(productId != null)
            alerts = alerts.stream()
                    .filter(a -> a.getProductId().equals(productId))
                    .collect(Collectors.toList());

        if(status != null)
            alerts = alerts.stream()
                    .filter(a -> a.getStatus().equals(status))
                    .collect(Collectors.toList());

        return alerts;
    }
}
