package application.topkingapi.entity.referral;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("referral")
public class ReferralController {
    private final ReferralOrchestrator referralOrchestrator;

    public ReferralController(ReferralOrchestrator referralOrchestrator) {
        this.referralOrchestrator = referralOrchestrator;
    }

    @PostMapping("/client/{clientName}/{clientId}/email/{referralEmail}")
    public void processReferral(
            @PathVariable String clientName,
            @PathVariable Long clientId,
            @PathVariable String referralEmail) throws Exception {
        referralOrchestrator.processReferral(clientName, referralEmail, clientId);
    }

}
