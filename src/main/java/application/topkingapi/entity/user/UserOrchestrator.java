package application.topkingapi.entity.user;

import application.topkingapi.entity.referral.ReferralService;
import application.topkingapi.mail.EmailSenderService;
import application.topkingapi.model.Referral;
import application.topkingapi.model.User;
import io.micrometer.common.util.StringUtils;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

    public User addUser(User userToCreate) throws Exception {
        if (ADMIN_EMAILS.contains(userToCreate.getEmail())) {
            userToCreate.setProductTier("admin");
        } else if (userToCreate.getProductTier() == null) {
            userToCreate.setProductTier("");
        } else {
            userToCreate.setProductTier(userToCreate.getProductTier());
        }
        var verificationCode = constructVerificationCode();
        userToCreate.setPasswordResetCode(verificationCode);
        var savedUser = userService.createUser(userToCreate);

        sendVerificationEmail(savedUser);
        syncWithReferral(savedUser);

        var userToReturn = new User();
        userToReturn.setId(savedUser.getId());
        return userToReturn;
    }

    private String constructVerificationCode() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    private void sendVerificationEmail(User savedUser) throws MessagingException {
        var subject = "Top King Training - Please verify your email!";
        var body = "Thank you for creating an account with us. Please enter this code onto the screen to verify your email:" +
                "<br/><br/><strong> " +
                savedUser.getPasswordResetCode() +
                "</strong>";
        emailSenderService.sendSimpleEmail(savedUser.getEmail(), subject, body);
    }

    private void syncWithReferral(User savedUser) throws Exception {
        Referral referrer = referralService.getReferrals()
                .stream()
                .filter(ref -> savedUser.getEmail().equals(ref.getEmail()))
                .findAny()
                .orElse(null);
        if (referrer != null) {
            // Send email to TKO saying user needs commission
            User referrerUserInfo = userService.getUserById(referrer.getAffiliateId());
            // TODO: When ready, send to ADMIN_EMAILS instead
            for (var adminEmail : List.of("medunn626@gmail.com")) {
                var subject = "Referral Account Created!";
                var body = "Please pay commission to the referrer. <br/>" +
                        "Name: " + referrerUserInfo.getName() + "<br/>" +
                        "Email: " + referrerUserInfo.getEmail() + "<br/>" +
                        "Payment Method: " + referrer.getPaymentMethod() + "<br/>" +
                        "Payment Handle: " + referrer.getPaymentHandle();
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
        if (StringUtils.isNotEmpty(savedUser.getPasswordResetCode())) {
            return userToReturn;
        }
        userToReturn.setProductTier(savedUser.getProductTier());
        userToReturn.setId(savedUser.getId());
        userToReturn.setName(savedUser.getName());
        userToReturn.setPhoneNumber(savedUser.getPhoneNumber());
        return userToReturn;
    }

    public void sendPasswordReset(String email) throws Exception {
        var matchingUser = userService.getAllUsers().stream()
                .filter(user -> email.equals(user.getEmail()))
                .findAny()
                .orElseThrow(() -> new Exception("Unable to find user with email"));
        var code = UUID.randomUUID().toString();
        matchingUser.setPasswordResetCode(code);
        userService.updateUser(matchingUser);

        var subject = "Top King Training - Password Reset";
        var body = "Please enter this code onto the screen to reset your password:" +
                "<br/><br/><strong>" +
                code +
                "</strong>";
        emailSenderService.sendSimpleEmail(email, subject, body);
    }

    public Long confirmCode(String code, String email) {
        return userService.getAllUsers()
                .stream()
                .filter(user -> email.equals(user.getEmail()))
                .filter(user -> code.equals(user.getPasswordResetCode()))
                .map(User::getId)
                .findAny()
                .orElse(null);
    }

    public User confirmCode(String code, Long id) throws Exception {
        var userToReturn = userService.getUserById(id);
        var confirmed = code.equals(userToReturn.getPasswordResetCode());
        if (confirmed) {
            userToReturn.setPasswordResetCode(null);
            userToReturn.setProductTier(userToReturn.getProductTier());
            userToReturn.setId(userToReturn.getId());
            userToReturn.setName(userToReturn.getName());
            userToReturn.setPhoneNumber(userToReturn.getPhoneNumber());
            return userService.updateUser(userToReturn);
        } else {
            return new User();
        }
    }

    public User changePassword(User userToSave) throws Exception {
        User matchingUser = userService.getUserById(userToSave.getId());
        matchingUser.setPassword(userToSave.getPassword());
        matchingUser.setPasswordResetCode(null);
        return userService.updateUser(matchingUser);
    }
}
