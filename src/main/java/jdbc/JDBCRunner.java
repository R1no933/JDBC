package jdbc;

import jdbc.util.ConnectionManager;

import org.postgresql.Driver;

import java.sql.Connection;
import java.sql.SQLException;

public class JDBCRunner {
    public static void main(String[] args) throws SQLException {
        Class<Driver> driverClass = Driver.class;

        try (Connection connection = ConnectionManager.openConncetion()) {
            System.out.println(connection.getTransactionIsolation());
        }
    }
}