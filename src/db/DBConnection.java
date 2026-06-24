package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/railway_db";
    private static final String USER = "root";
    private static final String PASSWORD = "yj123";

    public static Connection getConnection() {
        Connection conn = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (ClassNotFoundException e) {
            System.out.println("MySQL Driver not found: " + e.getMessage());

        } catch (SQLException e) {

    if (e.getMessage().contains("Access denied")) {
        System.out.println("Database username or password is incorrect.");
    }
    else if (e.getMessage().contains("Communications link failure")) {
        System.out.println("MySQL Server is not running.");
    }
    else {
        System.out.println("Database Error: " + e.getMessage());
    }
}
        return conn;
    }
}