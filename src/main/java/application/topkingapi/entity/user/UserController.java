package application.topkingapi.entity.user;

import application.topkingapi.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("")
public class UserController {
    private final UserOrchestrator userOrchestrator;

    public UserController(UserOrchestrator userOrchestrator) {
        this.userOrchestrator = userOrchestrator;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        var users = userOrchestrator.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<User> signUp(@RequestBody User userToCreate) throws Exception {
        var user = userOrchestrator.addUser(userToCreate);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/login")
    public ResponseEntity<User> login(@RequestBody User userToFind) throws Exception {
        var user = userOrchestrator.getAndReturnUser(userToFind);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/password-reset")
    public void sendPasswordReset(@RequestBody String email) throws Exception {
        userOrchestrator.sendPasswordReset(email);
    }

    @GetMapping("/confirm-code/{code}")
    public Long confirmCode(@PathVariable String code, @RequestParam String email) {
        return userOrchestrator.confirmCode(code, email);
    }

    @GetMapping("/confirm-code/user/{id}/code/{code}")
    public ResponseEntity<User> confirmCode(@PathVariable String code, @PathVariable Long id) throws Exception {
        var user = userOrchestrator.confirmCode(code, id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/change-password")
    public ResponseEntity<User> changePassword(@RequestBody User userToSave) throws Exception {
        var user = userOrchestrator.changePassword(userToSave);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("update-user")
    public ResponseEntity<User> updateUser(@RequestBody User userToUpdate) throws Exception {
        var user = userOrchestrator.updateUser(userToUpdate);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/remove-plan/{id}")
    public void removePlan(@PathVariable String id) throws Exception {
        userOrchestrator.removePlan(Long.parseLong(id));
    }

}
