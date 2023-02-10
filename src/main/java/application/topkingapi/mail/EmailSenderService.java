package application.topkingapi.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleEmail(String toEmail, String subject, String body) throws MessagingException {
        var mimeMessage = mailSender.createMimeMessage();
        mimeMessage.setFrom("fromemail@gmail.com");
        mimeMessage.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(toEmail));
        mimeMessage.setText(body);
        mimeMessage.setContent(body, "text/html; charset=utf-8");
        mimeMessage.setSubject(subject);
        mailSender.send(mimeMessage);
    }
}