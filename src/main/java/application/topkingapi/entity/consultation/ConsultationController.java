package application.topkingapi.entity.consultation;

import application.topkingapi.twilio.SmsRequest;
import application.topkingapi.twilio.TwilioService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("consultation")
public class ConsultationController {

    private static final String MESSAGE = "Thank you for signing up for a consulting session from the Top King! " +
            "You will receive a text with further instructions on how to book a time shortly";

    private final TwilioService twilioService;
    public ConsultationController(TwilioService twilioService) {
        this.twilioService = twilioService;
    }

    @PostMapping("/{phoneNumber}")
    public void setupConsultingCall(@PathVariable String phoneNumber) {
        var request = new SmsRequest(phoneNumber, MESSAGE);
        twilioService.sendSms(request);
    }
}
