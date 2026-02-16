package ineventory.Service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendResetLink(String email, String token){

        String resetLink="http://localhost:8080/auth/reset?token="+token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset-Inventra");
        message.setText("Click here to reset your password:\n"+resetLink);

        mailSender.send(message);
    }
}
