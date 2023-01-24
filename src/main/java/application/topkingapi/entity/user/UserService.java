package application.topkingapi.entity.user;

import application.topkingapi.model.User;
import application.topkingapi.repo.UserRepo;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Predicate;

@Service
public class UserService {
    private final UserRepo userRepo;

    @Autowired
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User addAndReturnUser(String email, String password, String productTier) throws Exception {
        var newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(password);
        if (productTier == null) {
            newUser.setProductTier("");
        } else {
            newUser.setProductTier(productTier);
        }
        var savedUser = userRepo.save(newUser);

        if (savedUser == null) {
            throw new Exception("Unable to create user");
        }

        var userToReturn = new User();
        userToReturn.setProductTier(savedUser.getProductTier());
        userToReturn.setId(savedUser.getId());
        return userToReturn;
    }

    public User getAndReturnUser(String email, String password, String productTier) throws Exception {
        Predicate<User> matchesEmail = user -> email.equals(user.getEmail());
        Predicate<User> matchesPassword = user -> password.equals(user.getPassword());
        var savedUser = userRepo.findAll().stream()
                .filter(matchesEmail.and(matchesPassword))
                .findAny()
                .orElseThrow(() -> new Exception("Unable to find user"));

        if (StringUtils.isNotEmpty(productTier)
                && Optional.ofNullable(savedUser.getProductTier()).filter(productTier::equals).isEmpty()
                && Optional.ofNullable(savedUser.getProductTier()).filter("admin"::equals).isEmpty()) {
            savedUser.setProductTier(productTier);
            userRepo.save(savedUser);
        }

        var userToReturn = new User();
        userToReturn.setProductTier(savedUser.getProductTier());
        userToReturn.setId(savedUser.getId());
        return userToReturn;
    }

    public User getUserById(Long id) throws Exception {
        return userRepo.findById(id).orElseThrow(() -> new Exception("Unable to find user with id"));
    }

    public User updateUser(User userToUpdate) {
        return userRepo.save(userToUpdate);
    }

    public void changePassword(String email, String password) throws Exception {
        Predicate<User> matchesEmail = user -> email.equals(user.getEmail());
        var userToUpdate = userRepo.findAll().stream()
                .filter(matchesEmail)
                .findAny()
                .orElseThrow(() -> new Exception("Unable to find existing user"));
        userToUpdate.setPassword(password);
        userRepo.save(userToUpdate);
    }
}
