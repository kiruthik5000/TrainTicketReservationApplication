package utils;

import exception.DataBaseException;

import java.sql.*;
import java.util.List;

public class DbUtils {
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/train", "root", "root");
        } catch (Exception e) {
            System.out.println("Unexpected Error Occurs Cannot connect to the db"+e.getMessage());
        }
        return null;
    }

    public static <T> void executeUpdateQuery(String q, List<T> objects) throws DataBaseException {
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(q)
                ) {
            for (int i=0; i<objects.size(); i++) {
                preparedStatement.setObject((i + 1), objects.get(i));
            }
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void executeInsertQuery(String q, List<T> objects) throws DataBaseException {
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(q)
                ) {

            for (int i=0; i<objects.size(); i++) {
                preparedStatement.setObject((i + 1), objects.get(i));
            }
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage());
        }
    }
}
