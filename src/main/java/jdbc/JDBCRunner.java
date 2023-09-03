package jdbc;

import jdbc.util.ConnectionManager;

import org.postgresql.Driver;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCRunner {
    public static void main(String[] args) throws SQLException {
        Class<Driver> driverClass = Driver.class;

        String sqlRequest = """
                CREATE TABLE IF NOT EXISTS info (
                    id SERIAL PRIMARY KEY ,
                    data TEXT NOT NULL 
                )
                """;

        try (Connection connection = ConnectionManager.openConncetion();
             Statement statement = connection.createStatement()) {
            System.out.println(connection.getTransactionIsolation());
            boolean executeResult = statement.execute(sqlRequest);
            System.out.println(executeResult);
        }
    }
}