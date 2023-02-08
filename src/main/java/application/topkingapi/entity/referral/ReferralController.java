package application.topkingapi.entity.referral;

import application.topkingapi.model.Referral;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("referral")
public class ReferralController {
    private final ReferralOrchestrator referralOrchestrator;

    public ReferralController(ReferralOrchestrator referralOrchestrator) {
        this.referralOrchestrator = referralOrchestrator;
    }

    @PostMapping()
    public void processReferral(@RequestBody Referral referralRequest) throws Exception {
        referralOrchestrator.processReferral(referralRequest);
    }

}
