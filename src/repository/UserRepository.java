package repository;

import model.User;

public interface UserRepository {
    User getUserByEmail(String email);
    User save(User user);
}
