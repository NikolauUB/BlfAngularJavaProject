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
        Properties props = new Properties();
        //props.put("mail.smtp.host", "smtp.googlemail.com");
        props.put("mail.from", "nikolaybarabanshchikov@gmail.com");
        props.put("mail.smtp.starttls.enable", "true");
        emailSender.setJavaMailProperties(props);

        //emailSender.setProtocol(JavaMailSenderImpl.DEFAULT_PROTOCOL);
        emailSender.setPort(587);
        emailSender.setHost("smtp.gmail.com");
        emailSender.setUsername("nikolaybarabanshchikov@gmail.com");
        emailSender.setPassword("Mordormordor12!");


        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Ntcnлодл");
        message.setText("Ссылка");
        emailSender.send(message);
    }

}
