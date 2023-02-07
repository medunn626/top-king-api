package application.topkingapi.entity.referral;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("referral")
public class ReferralController {
    private final ReferralOrchestrator referralOrchestrator;

    public ReferralController(ReferralOrchestrator referralOrchestrator) {
        this.referralOrchestrator = referralOrchestrator;
    }

    @PostMapping("/client/{clientId}/payment/{paymentMethod}/{paymentHandle}/email/{referralEmail}")
    public void processReferral(
            @PathVariable Long clientId,
            @PathVariable String paymentMethod,
            @PathVariable String paymentHandle,
            @PathVariable String referralEmail) throws Exception {
        referralOrchestrator.processReferral(referralEmail, paymentMethod, paymentHandle, clientId);
    }

}
