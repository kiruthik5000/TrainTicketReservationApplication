package repository.Implementation.InMemory;

import model.User;
import repository.UserRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository implements UserRepository {

    private final Map<Integer, User> userMap;
    private final Map<String, Integer> emailUserMap;
    private int nextUserId;

    public InMemoryUserRepository() {
        nextUserId = 1;

        emailUserMap = new ConcurrentHashMap<>();
        userMap = new ConcurrentHashMap<>();

        emailUserMap.put("admin@gmail.com", 1);
        userMap.put(1, new User(1, "admin", "admin@gmail.com", "admin@123"));
    }


    @Override
    public User getUserByEmail(String email) {
        return userMap.get(emailUserMap.get(email));
    }

    @Override
    public void save(User user) {
        user.setUserId(++nextUserId);
        userMap.put(nextUserId, user);
    }
}
