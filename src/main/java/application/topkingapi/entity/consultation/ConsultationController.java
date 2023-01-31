package application.topkingapi.entity.consultation;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("consultation")
public class ConsultationController {

    private final ConsultationOrchestrator consultationOrchestrator;
    public ConsultationController(ConsultationOrchestrator consultationOrchestrator) {
        this.consultationOrchestrator = consultationOrchestrator;
    }

    @PostMapping("/{phoneNumber}")
    public void setupConsultingCall(@PathVariable String phoneNumber) {
        consultationOrchestrator.setupConsultingCall(phoneNumber);
    }
}
