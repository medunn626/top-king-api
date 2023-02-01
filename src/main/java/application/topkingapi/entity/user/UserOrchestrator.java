package application.topkingapi.entity.user;

import application.topkingapi.entity.referral.ReferralService;
import application.topkingapi.mail.EmailSenderService;
import application.topkingapi.model.Referral;
import application.topkingapi.model.User;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Component
public class UserOrchestrator {
    private UserService userService;
    private ReferralService referralService;
    private EmailSenderService emailSenderService;

    public final static List<String> ADMIN_EMAILS = List.of("kingtko1992@gmail.com");

    public UserOrchestrator(
            UserService userService,
            ReferralService referralService,
            EmailSenderService emailSenderService) {
        this.userService = userService;
        this.referralService = referralService;
        this.emailSenderService = emailSenderService;
    }

    public User addAndReturnUser(User userToCreate) throws Exception {
        if (ADMIN_EMAILS.contains(userToCreate.getEmail())) {
            userToCreate.setProductTier("admin");
        } else if (userToCreate.getProductTier() == null) {
            userToCreate.setProductTier("");
        } else {
            userToCreate.setProductTier(userToCreate.getProductTier());
        }
        var savedUser = userService.createUser(userToCreate);

        if (savedUser == null) {
            throw new Exception("Error creating user");
        }

        syncWithReferral(savedUser);

        var userToReturn = new User();
        userToReturn.setProductTier(savedUser.getProductTier());
        userToReturn.setId(savedUser.getId());
        userToReturn.setPhoneNumber(savedUser.getPhoneNumber());
        return userToReturn;
    }

    private void syncWithReferral(User savedUser) throws Exception {
        var affiliateId = referralService.getReferrals()
                .stream()
                .filter(ref -> savedUser.getEmail().equals(ref.getEmail()))
                .findAny()
                .map(Referral::getAffiliateId);
        if (affiliateId.isPresent()) {
            // Send email to TKO saying user needs commission
            var affiliate = userService.getUserById(affiliateId.get());
            var affiliateEmail = affiliate.getEmail();
            // TODO: When ready, send to ADMIN_EMAILS instead
            for (var adminEmail : List.of("medunn626@gmail.com")) {
                var subject = "Referral Account Created!";
                var body = "Please pay commission to user associated with " + affiliateEmail;
                emailSenderService.sendSimpleEmail(adminEmail, subject, body);
            }
        }
    }

    public User getAndReturnUser(String email, String password, String productTier) throws Exception {
        Predicate<User> matchesEmail = user -> email.equals(user.getEmail());
        Predicate<User> matchesPassword = user -> password.equals(user.getPassword());
        var savedUser = userService.getAllUsers().stream()
                .filter(matchesEmail.and(matchesPassword))
                .findAny()
                .orElseThrow(() -> new Exception("Unable to find user"));

        if (StringUtils.isNotEmpty(productTier)
                && Optional.ofNullable(savedUser.getProductTier()).filter(productTier::equals).isEmpty()
                && Optional.ofNullable(savedUser.getProductTier()).filter("admin"::equals).isEmpty()) {
            savedUser.setProductTier(productTier);
            userService.updateUser(savedUser);
        }

        var userToReturn = new User();
        userToReturn.setProductTier(savedUser.getProductTier());
        userToReturn.setId(savedUser.getId());
        userToReturn.setPhoneNumber(savedUser.getPhoneNumber());
        return userToReturn;
    }
}
