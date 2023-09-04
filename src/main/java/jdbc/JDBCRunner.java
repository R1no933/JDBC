package jdbc;

import jdbc.util.ConnectionManager;

import org.postgresql.Driver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCRunner {
    public static void main(String[] args) throws SQLException {
        Class<Driver> driverClass = Driver.class;

        String sqlDDL = """
                CREATE TABLE IF NOT EXISTS info (
                    id SERIAL PRIMARY KEY ,
                    data TEXT NOT NULL 
                )
                """;

        String sqlDML = """
                INSERT INTO info (data)
                VALUES
                ('TEST_1'),
                ('TEST_2'),
                ('TEST_3'),
                ('TEST_4')
                """;

        String sqlSelect = """
                SELECT * FROM flight
                """;

        try (Connection connection = ConnectionManager.openConncetion();
             Statement statement = connection.createStatement()) {
            System.out.println(connection.getTransactionIsolation());

            boolean executeResultDDL = statement.execute(sqlDDL);
            System.out.println(executeResultDDL);

            int executeResultDML = statement.executeUpdate(sqlDML);
            System.out.println(executeResultDML);

            ResultSet selectResult = statement.executeQuery(sqlSelect);
            while (selectResult.next()) {
                System.out.println(selectResult.getLong("id"));
                System.out.println(selectResult.getString("flight_no"));
                System.out.println(selectResult.getString("status"));
                System.out.println("===============");
            }
        }
    }
}