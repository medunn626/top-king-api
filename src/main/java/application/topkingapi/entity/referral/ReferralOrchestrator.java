package application.topkingapi.entity.referral;

import application.topkingapi.entity.user.UserService;
import application.topkingapi.mail.EmailSenderService;
import application.topkingapi.model.Referral;
import application.topkingapi.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public void processReferral(Referral referralRequest) throws Exception {
        var allUsers = userService.getAllUsers();
        var affiliate = allUsers.stream()
                .filter(user -> user.getId().equals(referralRequest.getAffiliateId()))
                .findAny()
                .orElseThrow(() -> new Exception(("Cannot find matching affiliate id " + referralRequest.getAffiliateId())));
        if (isExistingUser(allUsers, referralRequest.getEmail())) {
            // Send email to client saying they don't qualify
            var affiliateEmail = affiliate.getEmail();
            var subject = "Top King Training - Your referral recommendation already has an account with us";
            var body = "Apologies. Your referral already has an account with us, " +
                    "therefore you do not qualify for commission for this recommendation";
            emailSenderService.sendSimpleEmail(affiliateEmail, subject, body);
        } else if (isDuplicateReferral(referralRequest.getEmail())) {
            // Send email to client saying they don't qualify
            var affiliateEmail = affiliate.getEmail();
            var subject = "Top King Training - Your referral recommendation was already referred";
            var body = "Apologies. Your referral has already been referred by another client, " +
                    "therefore you do not qualify for commission for this recommendation";
            emailSenderService.sendSimpleEmail(affiliateEmail, subject, body);
        } else {
            referralService.createReferral(referralRequest);
            // Email the referral
            var subject = affiliate.getName() + " recommends you join TopKing!";
            var body = "What's up! <br/><br/>" +
                    affiliate.getName() +
                    " recommends you sign up for fitness training with the Top King!" +
                    " Click <a href='https://top-kingtraining.com'>here</a>" +
                    " to join and let's get started.";

            emailSenderService.sendSimpleEmail(referralRequest.getEmail(), subject, body);
        }
    }

    private boolean isExistingUser(List<User> users, String email) {
        return users.stream()
                .map(User::getEmail)
                .anyMatch(email::equals);
    }

    private boolean isDuplicateReferral(String email) {
        return referralService.getReferrals()
                .stream()
                .map(Referral::getEmail)
                .anyMatch(email::equals);
    }
}
