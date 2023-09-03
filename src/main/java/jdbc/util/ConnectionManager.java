package jdbc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/flight_repository";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "P@ssw0rd";

    static {
        loadDriver();
    }

    private ConnectionManager() {}

    public static Connection openConncetion() {
        try {
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException error) {
            throw new RuntimeException(error);
        }
    }

    private static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException error) {
            throw new RuntimeException(error);
        }
    }
}
