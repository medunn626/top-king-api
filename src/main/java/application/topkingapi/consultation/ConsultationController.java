package application.topkingapi.consultation;

import jakarta.mail.MessagingException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("")
public class ConsultationController {
    private final ConsultationOrchestrator consultationOrchestrator;

    public ConsultationController(ConsultationOrchestrator consultationOrchestrator) {
        this.consultationOrchestrator = consultationOrchestrator;
    }

    @PostMapping("/consultation")
    public void setupConsultingCall(@RequestBody String email) throws MessagingException {
        consultationOrchestrator.setupConsultingCall(email);
    }
}
