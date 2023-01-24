package application.topkingapi.entity.product;

import application.topkingapi.entity.user.UserService;
import application.topkingapi.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static application.topkingapi.entity.user.UserService.ADMIN_EMAILS;

@RestController
@RequestMapping("/plans")
public class ProductController {
    private final UserService userService;

    public ProductController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/user/{userId}/plan/{productTier}")
    public ResponseEntity<User> updatePlan(@PathVariable String userId,
                                           @PathVariable String productTier) throws Exception {
        var id = Long.parseLong(userId);
        var currentUser = userService.getUserById(id);
        if (!ADMIN_EMAILS.contains(currentUser.getEmail())) {
            currentUser.setProductTier(productTier);
        }
        var updatedUser = userService.updateUser(currentUser);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

}
