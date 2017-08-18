package wind.instrument.competitions.middle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Properties;


@Component
public class SendEmailToUser {
    //@Autowired
    private JavaMailSenderImpl emailSender = new JavaMailSenderImpl();

    public void sendLinkForResetPassword(String email) {



        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Тест");
        message.setText("Ссылка");
        emailSender.send(message);
    }

}
