package jdbc;

import jdbc.util.ConnectionManager;

import org.postgresql.Driver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JDBCRunner {
    public static void main(String[] args) throws SQLException {
        Class<Driver> driverClass = Driver.class;

        String flightId = "2 OR 1 = 1"; //injection can make there (2 OR 1 = 1; DROP BASE info etc
        List<Long> resultSqlInjection = getTicketsById(flightId);
        System.out.println(resultSqlInjection);
        System.out.println("===============================");

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

        String sqlGenerator = """
                INSERT INTO info (data)
                VALUES 
                ('autogenerating')
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

            int executeGeneratedResult = statement.executeUpdate(sqlGenerator, Statement.RETURN_GENERATED_KEYS);
            ResultSet generatedKey = statement.getGeneratedKeys();
            if (generatedKey.next()) {
                int generatedId = generatedKey.getInt("id");
                System.out.println(generatedId);
            }
        }


    }

    private static List<Long> getTicketsById(String flightId) throws SQLException {
        String sql = """
                    SELECT id
                    FROM ticket
                    WHERE flight_id = %s
                    """.formatted(flightId);

        List<Long> resultList = new ArrayList<>();

        try (Connection connection = ConnectionManager.openConncetion();
            Statement statement = connection.createStatement()) {

            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                resultList.add(result.getObject("id", Long.class)); //NULL-SAFE
            }
        }
        return resultList;
    }
}