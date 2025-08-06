package hexlet.code.utils;


import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {
    @Autowired
    private UserRepository userRepository;

    private static final String ADMIN_FIRST_NAME = "hexlet";
    private static final String ADMIN_EMAIL = "hexlet@example.com";
    private static final String ADMIN_PASSWORD = "123";

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        var email = authentication.getName();
        return userRepository.findByEmail(email).get();
    }

    public boolean isOwner(Long id) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        var currentUserEmail = authentication.getName();
        return userRepository.findById(id)
                .map(user -> user.getEmail().equals(currentUserEmail))
                .orElse(false);
    }

    public User getAdminUser() {
        User adminUser = new User();
        adminUser.setFirstName(ADMIN_FIRST_NAME);
        adminUser.setEmail(ADMIN_EMAIL);
        adminUser.setPasswordDigest(UserUtils.ADMIN_PASSWORD);
        return adminUser;
    }
}
