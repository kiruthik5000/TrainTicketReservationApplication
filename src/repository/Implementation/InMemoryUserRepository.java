package repository.Implementation;

import model.User;
import repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryUserRepository implements UserRepository {

    private final Map<Integer, User> userMap;
    private int nextUserId;

    public InMemoryUserRepository() {
        nextUserId = 1;
        userMap = new HashMap<>();
        userMap.put(1, new User(1, "admin", "admin@gmail.com", "admin@123"));
    }


    @Override
    public User getUserByEmail(String email) {
        return userMap.values()
                .stream()
                .filter(k -> k.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public User save(User user) {
        user.setUserId(++nextUserId);
        userMap.put(nextUserId, user);
        return user;
    }
}
