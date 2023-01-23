package application.topkingapi.entity.user;

import application.topkingapi.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<User> signUp(@RequestParam String email,
                                       @RequestParam String password,
                                       @RequestParam String productTier) throws Exception {
        var user = userService.addAndReturnUser(email, password, productTier);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/login")
    public ResponseEntity<User> login(@RequestParam String email,
                                         @RequestParam String password,
                                         @RequestParam String productTier) throws Exception {
        var user = userService.getAndReturnUser(email, password, productTier);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/change-password")
    public void changePassword(@RequestParam String email, @RequestParam String password) throws Exception {
        userService.changePassword(email, password);
    }

}
