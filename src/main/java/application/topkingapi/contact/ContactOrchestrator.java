package application.topkingapi.contact;

import application.topkingapi.mail.EmailSenderService;
import application.topkingapi.model.Contact;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ContactOrchestrator {
    private final EmailSenderService emailSenderService;

    private static final String SUBJECT = "New Inquiry from Your Training Website!";
    @Value("${spring.mail.username}")
    private String adminEmailAddr;

    public ContactOrchestrator(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    public void sendInquiry(Contact contactRequest) throws MessagingException {
        var message = "New inquiry from " + contactRequest.getName() + " at " + contactRequest.getEmail() +
                "<br/><br/>" +
                contactRequest.getMessage();
        emailSenderService.sendSimpleEmail(adminEmailAddr, SUBJECT, message);
    }
}
