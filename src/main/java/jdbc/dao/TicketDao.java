package jdbc.dao;

import jdbc.entity.TicketEntity;
import jdbc.exception.DaoExceptions;
import jdbc.util.ConnectionManagerWithPool;

import java.sql.*;

public class TicketDao {

    private static final TicketDao INSTANCE = new TicketDao();
    private static final String DELETE_SQL = """
            DELETE FROM ticket
            WHERE id = ?
            """;
    public static final String SAVE_SQL = """
            INSERT INTO ticket (passenger_no, passenger_name, flight_id, seat_no, cost) 
            VALUES (?, ?, ?, ?, ?) 
            """;

    private TicketDao() {}

    public TicketEntity saveTicket(TicketEntity ticket) {
        try (Connection connection = ConnectionManagerWithPool.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, ticket.getPassengerNo());
            preparedStatement.setString(2, ticket.getPassengerName());
            preparedStatement.setLong(3, ticket.getFlightId());
            preparedStatement.setString(4, ticket.getSeatNo());
            preparedStatement.setBigDecimal(5, ticket.getCost());

            preparedStatement.executeUpdate();
            ResultSet generatedKey = preparedStatement.getGeneratedKeys();
            if (generatedKey.next()) {
                ticket.setId(generatedKey.getLong("id"));
            }
            return ticket;
        } catch (SQLException throwables) {
            throw new DaoExceptions(throwables);
        }
    }

    public boolean deleteTicket(Long id) {
        try (Connection connection = ConnectionManagerWithPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setLong(1, id);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException throwables) {
            throw new DaoExceptions(throwables);
        }
    }

    public static TicketDao getInstance() {
        return INSTANCE;
    }
}
