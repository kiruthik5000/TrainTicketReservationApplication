package repository.Implementation.DataBase;

import exception.DataBaseException;
import model.User;
import repository.UserRepository;
import utils.DbUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class DbUserRepository implements UserRepository {

    @Override
    public User getUserByEmail(String email) throws DataBaseException {
        String query = "SELECT * FROM user WHERE email = ?;";
        try (
                Connection connection = DbUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setObject(1, email);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("userId"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password")
                );
            }
            return null;
        }catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
    }
    @Override
    public void save(User user) throws DataBaseException {
        String query = "INSERT INTO user(username, email, password) VALUES (?,?,?);";

       try{
            DbUtils.executeUpdateQuery(query, List.of(
                    user.getUsername(),
                    user.getEmail(),
                    user.getPassword()
            ));
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
    }
}
