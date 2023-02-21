package application.topkingapi.consultation;

import application.topkingapi.mail.EmailSenderService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConsultationOrchestrator {
    private final EmailSenderService emailSenderService;

    private static final String CLIENT_SUBJECT = "Top King Training - Consultation!";
    private static final String CLIENT_MESSAGE = "Thank you for signing up for a consulting session with the Top King! " +
            "You will receive a message with date and time options shortly.";
    private static final String ADMIN_EMAIL_SUBJECT = "New Consulting Request";

    @Value("${spring.mail.username}")
    private String adminEmailAddr;

    public ConsultationOrchestrator(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    public void setupConsultingCall(String email) throws MessagingException {
        sendEmailToClient(email);
        sendEmailToAdmin(email);
    }

    private void sendEmailToClient(String email) throws MessagingException {
        emailSenderService.sendSimpleEmail(email, CLIENT_SUBJECT, CLIENT_MESSAGE);
    }

    private void sendEmailToAdmin(String email) throws MessagingException {
        var adminMessage = "Client at email address " +
                "<strong>" + email + "</strong>"+
                " has requested a consulting session. " +
                "Please contact the client to coordinate.";
        emailSenderService.sendSimpleEmail(adminEmailAddr, ADMIN_EMAIL_SUBJECT, adminMessage);
    }
}
