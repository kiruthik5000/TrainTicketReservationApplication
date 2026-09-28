package repository;

import exception.DataBaseException;
import model.User;

public interface UserRepository {
    User getUserByEmail(String email) throws DataBaseException;
    void save(User user) throws DataBaseException;
}
