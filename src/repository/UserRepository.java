package repository;

import model.User;

public interface UserRepository {
    User getUserByEmail(String email);
    boolean save(User user);
}
