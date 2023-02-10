package application.topkingapi.consultation;

import application.topkingapi.mail.EmailSenderService;
import application.topkingapi.twilio.SmsRequest;
import application.topkingapi.twilio.TwilioService;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

@Component
public class ConsultationOrchestrator {
    private final TwilioService twilioService;
    private final EmailSenderService emailSenderService;
    private static final String CLIENT_MESSAGE = "Thank you for signing up for a consulting session with the Top King! " +
            "You will receive a text with date and time options shortly.";

    private static final String ADMIN_EMAIL_ADDR = "medunn626@gmail.com";
//    private static final String ADMIN_EMAIL_ADDR = "kingtko1992@gmail.com";
    private static final String ADMIN_EMAIL_SUBJECT = "New Consulting Request";

    public ConsultationOrchestrator(
            TwilioService twilioService,
            EmailSenderService emailSenderService
    ) {
        this.twilioService = twilioService;
        this.emailSenderService = emailSenderService;
    }

    public void setupConsultingCall(@PathVariable String phoneNumber) throws MessagingException {
        sentTextToClient(phoneNumber);
        sendEmailToAdmin(phoneNumber);
    }

    private void sentTextToClient(String phoneNumber) {
        var request = new SmsRequest(phoneNumber, CLIENT_MESSAGE);
        twilioService.sendSms(request);
    }

    private void sendEmailToAdmin(String phoneNumber) throws MessagingException {
        var body = "Client at phone number " + phoneNumber + " has requested a consulting call. " +
                "Please contact the client to coordinate.";
        emailSenderService.sendSimpleEmail(ADMIN_EMAIL_ADDR, ADMIN_EMAIL_SUBJECT, body);
    }
}
