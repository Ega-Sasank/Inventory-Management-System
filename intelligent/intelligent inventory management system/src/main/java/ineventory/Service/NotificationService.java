package ineventory.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import ineventory.Entity.Alert;
import ineventory.Entity.Role;
import ineventory.Entity.User;
import ineventory.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepository userRepository;

    @Value("${twilio.account.sid}")
    private String ACCOUNT_SID;

    @Value("${twilio.account.token}")
    private String AUTH_TOKEN;

    public void sendAlert(Alert alert){

        // Dashboard notification
        System.out.println("ALERT: " + alert.getMessage());

        // Future upgrade
        // send email
        sendEmail(alert);
        // send SMS
        sendSMS(alert);
    }

    private void sendEmail(Alert alert){

        List<User> admins = userRepository.findByRole(Role.ADMIN);

        for(User admin : admins){

            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(admin.getEmail());
            message.setSubject("Inventory Alert");
            message.setText(alert.getMessage());

            mailSender.send(message);
        }
    }

    private void sendSMS(Alert alert){

//        c9301591ae4c698fb65dfc431b0c8";// your sid
//        0565f34f74bcb8b27c927a9c5301d51a";// auth token

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message.creator(
                new PhoneNumber("+916302230323"),
                new PhoneNumber("+13502180430"),
                alert.getMessage()
        ).create();

    }

    public void sendSummary(String message){

        List<User> admins = userRepository.findByRole(Role.ADMIN);

        for(User admin : admins){

            SimpleMailMessage mail = new SimpleMailMessage();

            mail.setTo(admin.getEmail());
            mail.setSubject("Daily Inventory Alert Summary");
            mail.setText(message);

            mailSender.send(mail);
        }

        sendSMSMessage(message);
    }
    private void sendSMSMessage(String message){



        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message.creator(
                new PhoneNumber("+916302230323"),
                new PhoneNumber("+13502180430"),
                message
        ).create();
    }
}
