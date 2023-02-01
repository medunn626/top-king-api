package application.topkingapi.entity.user;

import application.topkingapi.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("")
public class UserController {
    private final UserOrchestrator userOrchestrator;

    public UserController(UserOrchestrator userOrchestrator) {
        this.userOrchestrator = userOrchestrator;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<User> signUp(@RequestBody User userToCreate) throws Exception {
        var user = userOrchestrator.addAndReturnUser(userToCreate);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/login")
    public ResponseEntity<User> login(@RequestParam String email,
                                      @RequestParam String password,
                                      @RequestParam String productTier) throws Exception {
        var user = userOrchestrator.getAndReturnUser(email, password, productTier);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
