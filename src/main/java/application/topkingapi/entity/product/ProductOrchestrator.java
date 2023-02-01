package application.topkingapi.entity.product;

import application.topkingapi.entity.user.UserService;
import application.topkingapi.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static application.topkingapi.entity.user.UserOrchestrator.ADMIN_EMAILS;

@Component
public class ProductOrchestrator {
    private final UserService userService;
    public ProductOrchestrator(UserService userService) {
        this.userService = userService;
    }

    public ResponseEntity<User> updatePlan(String userId, String productTier) throws Exception {
        var id = Long.parseLong(userId);
        var currentUser = userService.getUserById(id);
        if (!ADMIN_EMAILS.contains(currentUser.getEmail())) {
            currentUser.setProductTier(productTier);
        }
        var updatedUser = userService.updateUser(currentUser);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }
}
