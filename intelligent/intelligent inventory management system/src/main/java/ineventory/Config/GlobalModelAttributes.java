package ineventory.Config;

import ineventory.Service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @Autowired
    private AlertService alertService;


        @ModelAttribute("alertCount")
        public int alertCount() {
            return alertService.getActiveAlerts().size();
        }


}
