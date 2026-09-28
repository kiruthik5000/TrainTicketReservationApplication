package utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbUtils {
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/train", "root", "root");
        } catch (Exception e) {
            System.out.println("Unexpected Error Occurs Cannot connect to the db"+e.getMessage());
        }
        return null;
    }

    public static void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (Exception e) {
            System.out.println("Unexpected Error Occurs Cannot close connection"+e.getMessage());
        }
    }
}
