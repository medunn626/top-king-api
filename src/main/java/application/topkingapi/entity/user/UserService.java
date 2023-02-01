package application.topkingapi.entity.user;

import application.topkingapi.model.User;
import application.topkingapi.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepo userRepo;

    @Autowired
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User createUser(User userToCreate) {
        return userRepo.save(userToCreate);
    }

    public User getUserById(Long id) throws Exception {
        return userRepo.findById(id).orElseThrow(() -> new Exception("Unable to find user with id"));
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User updateUser(User userToUpdate) {
        return userRepo.save(userToUpdate);
    }
}
