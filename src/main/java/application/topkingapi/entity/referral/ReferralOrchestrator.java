package application.topkingapi.entity.referral;

import application.topkingapi.entity.user.UserService;
import application.topkingapi.mail.EmailSenderService;
import application.topkingapi.model.Referral;
import org.springframework.stereotype.Component;

@Component
public class ReferralOrchestrator {
    private final ReferralService referralService;
    private final UserService userService;
    private final EmailSenderService emailSenderService;

    public ReferralOrchestrator(
            ReferralService referralService,
            UserService userService,
            EmailSenderService emailSenderService) {
        this.referralService = referralService;
        this.userService = userService;
        this.emailSenderService = emailSenderService;
    }

    public void processReferral(String clientName, String referralEmail, Long clientId) throws Exception {
        var affiliate = userService.getUserById(clientId);
        if (isDuplicate(referralEmail)) {
            // Send email to client saying they don't qualify
            var clientEmail = affiliate.getEmail();
            var subject = "Your referral recommendation was already referred";
            var body = "Apologies. Your referral has already been referred by another client, " +
                    "therefore you do not qualify for commission for this recommendation";
            emailSenderService.sendSimpleEmail(clientEmail, subject, body);
        } else {
            referralService.createReferral(referralEmail, affiliate);
            // Email the referral
            var subject = clientName + " recommends you join TopKing!";
            var body = "What's up! " + clientName +
                    " recommends you sign up for fitness training with the Top King!" +
                    " Click here to join and let's get started: https://medunn626.github.io/top-king";
            emailSenderService.sendSimpleEmail(referralEmail, subject, body);
        }
    }

    private boolean isDuplicate(String email) {
        return referralService.getReferrals()
                .stream()
                .map(Referral::getEmail)
                .anyMatch(email::equals);
    }
}
