package application.topkingapi.contact;

import application.topkingapi.mail.EmailSenderService;
import application.topkingapi.model.Contact;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Component;

@Component
public class ContactOrchestrator {
    private final EmailSenderService emailSenderService;
    private static final String ADMIN_EMAIL_ADDR = "medunn626@gmail.com";
//    private static final String ADMIN_EMAIL_ADDR = "kingtko1992@gmail.com";

    public ContactOrchestrator(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    public void sendInquiry(Contact contactRequest) throws MessagingException {
        var subject = "New Inquiry from Your Training Website!";
        var body = "New inquiry from " + contactRequest.getName() + " at " + contactRequest.getEmail() +
                "<br/><br/>" +
                contactRequest.getMessage();
        emailSenderService.sendSimpleEmail(ADMIN_EMAIL_ADDR, subject, body);
    }
}
