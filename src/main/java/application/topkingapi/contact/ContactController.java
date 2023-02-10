package application.topkingapi.contact;

import application.topkingapi.consultation.ConsultationOrchestrator;
import application.topkingapi.model.Contact;
import jakarta.mail.MessagingException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("contact")
public class ContactController {
    private final ContactOrchestrator contactOrchestrator;

    public ContactController(ContactOrchestrator contactOrchestrator) {
        this.contactOrchestrator = contactOrchestrator;
    }

    @PostMapping("")
    public void sendInquiry(@RequestBody Contact contactRequest) throws MessagingException {
        contactOrchestrator.sendInquiry(contactRequest);
    }
}
