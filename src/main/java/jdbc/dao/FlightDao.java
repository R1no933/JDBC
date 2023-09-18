package jdbc.dao;

import jdbc.entity.FlightEntity;
import jdbc.exception.DaoExceptions;
import jdbc.util.ConnectionManagerWithPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class FlightDao implements Dao<Long, FlightEntity> {

    private static final FlightDao INSTANCE = new FlightDao();
    private static final String FIND_BY_ID_SQL = """
            SELECT id,
                flight_no,
                departure_date,
                departure_airport_code,
                arrival_date,
                arrival_airport_code,
                aircraft_id,
                status
            FROM flight
            WHERE id = ?
            """;

    private FlightDao() {}

    public static FlightDao getINSTANCE() {
        return INSTANCE;
    }

    @Override
    public boolean deleteTicket(Long id) {
        return false;
    }

    @Override
    public FlightEntity saveTicket(FlightEntity ticket) {
        return null;
    }

    @Override
    public void updateTicket(FlightEntity ticket) {

    }


    @Override
    public Optional<FlightEntity> findTicketById(Long id) {
        try (Connection connection = ConnectionManagerWithPool.getConnection()) {
            return findTicketById(id, connection);
        } catch (SQLException throwables) {
            throw new DaoExceptions(throwables);
        }
    }

    public Optional<FlightEntity> findTicketById(Long id, Connection connection) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            FlightEntity flightEntity = null;

            if (resultSet.next()) {
                flightEntity = new FlightEntity(
                        resultSet.getLong("id"),
                        resultSet.getString("flight_no"),
                        resultSet.getTimestamp("departure_date").toLocalDateTime(),
                        resultSet.getString("departure_airport_code"),
                        resultSet.getTimestamp("arrival_date").toLocalDateTime(),
                        resultSet.getString("arrival_airport_code"),
                        resultSet.getInt("aircraft_id"),
                        resultSet.getString("status")
                );
            }

            return Optional.ofNullable(flightEntity);
        } catch (SQLException throwables) {
            throw new DaoExceptions(throwables);
        }
    }

    @Override
    public List<FlightEntity> findAll() {
        return null;
    }
}
