package application.topkingapi.entity.user.product;

import application.topkingapi.entity.user.UserOrchestrator;
import application.topkingapi.entity.user.UserService;
import application.topkingapi.model.User;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductOrchestrator {
    private final UserService userService;
    private final UserOrchestrator userOrchestrator;

    @Value("${spring.mail.username}")
    private String adminEmailAddr;

    public ProductOrchestrator(
            UserService userService,
            UserOrchestrator userOrchestrator) {
        this.userService = userService;
        this.userOrchestrator = userOrchestrator;
    }

    public ResponseEntity<User> updatePlan(String userId, String productTier) throws Exception {
        var id = Long.parseLong(userId);
        var currentUser = userService.getUserById(id);
        var addingPlanForFirstTime = StringUtils.isEmpty(currentUser.getProductTier());
        if (adminEmailAddr.equals(currentUser.getEmail())) {
            currentUser.setProductTier(productTier);
        }
        var updatedUser = userService.updateUser(currentUser);
        if (addingPlanForFirstTime) {
            userOrchestrator.syncWithReferral(updatedUser);
        }
        updatedUser.setPassword(null);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }
}
