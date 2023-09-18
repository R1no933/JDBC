package jdbc.dao;

import jdbc.dto.TicketEntityFilter;
import jdbc.entity.FlightEntity;
import jdbc.entity.TicketEntity;
import jdbc.exception.DaoExceptions;
import jdbc.util.ConnectionManagerWithPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TicketDao implements Dao<Long, TicketEntity> {

    private static final TicketDao INSTANCE = new TicketDao();

    private static final String DELETE_SQL = """
            DELETE FROM ticket
            WHERE id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO ticket (passenger_no, passenger_name, flight_id, seat_no, cost) 
            VALUES (?, ?, ?, ?, ?) 
            """;
    private static final String UPDATE_SQL = """
            UPDATE ticket
            SET 
                passenger_no = ?,
                passenger_name = ?,
                flight_id = ?,
                seat_no = ?,
                cost = ?
            WHERE id = ?
            """;
    private static final String FIND_ALL_SQL = """
            SELECT public.ticket.id,
                passenger_no,
                passenger_name,
                flight_id,
                seat_no,
                cost,
                f.status,
                f.aircraft_id,
                f.arrival_airport_code,
                f.arrival_date,
                f.departure_airport_code,
                f.departure_date,
                f.flight_no
            FROM ticket
            JOIN public.flight f
                ON f.id = ticket.flight_id
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE public.ticket.id = ?
            """;
    private final FlightDao flightDao = FlightDao.getINSTANCE();

    private TicketDao() {}

    public List<TicketEntity> findAllWithFilter(TicketEntityFilter filter) {
        List<Object> filterParameters = new ArrayList<>();
        List<String> whereSql = new ArrayList<>();
        if (filter.seatNo() != null) {
            whereSql.add("seat_no LIKE ?");
            filterParameters.add("%" + filter.seatNo() + "%");
        }

        if (filter.passengerName() != null) {
            whereSql.add("passenger_name = ?");
            filterParameters.add(filter.passengerName());
        }
        filterParameters.add(filter.limit());
        filterParameters.add(filter.offset());

        String where = whereSql.stream()
                .collect(Collectors.joining(" AND "," WHERE ", " LIMIT ? OFFSET ? "));

        String sql = FIND_ALL_SQL + where;
        try (Connection connection = ConnectionManagerWithPool.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < filterParameters.size(); i++) {
                preparedStatement.setObject(i + 1, filterParameters.get(i));
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            List<TicketEntity> tickets = new ArrayList<>();
            while (resultSet.next()) {
                 tickets.add(buildTicket(resultSet));
            }

            return tickets;
        } catch (SQLException throwables) {
            throw new DaoExceptions(throwables);
        }
    }

    public List<TicketEntity> findAll() {
        try (Connection connection = ConnectionManagerWithPool.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<TicketEntity> tickets = new ArrayList<>();
            while (resultSet.next()) {
                tickets.add(buildTicket(resultSet));
            }
            return tickets;
        } catch (SQLException throwables) {
            throw new DaoExceptions(throwables);
        }
    }

    public Optional<TicketEntity> findTicketById(Long id) {
        try (Connection connection = ConnectionManagerWithPool.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            TicketEntity ticket = null;
            if (resultSet.next()) {
                ticket = buildTicket(resultSet);
            }

            return Optional.ofNullable(ticket);
        } catch (SQLException throwables) {
            throw new DaoExceptions(throwables);
        }
    }

    private TicketEntity buildTicket(ResultSet resultSet) throws SQLException {
        return new TicketEntity(
                resultSet.getLong("id"),
                resultSet.getString("passenger_no"),
                resultSet.getString("passenger_name"),
                flightDao.findTicketById(resultSet.getLong("flight_id")).orElse(null),
                resultSet.getString("seat_no"),
                resultSet.getBigDecimal("cost")
        );
    }

    public void updateTicket(TicketEntity ticket) {
        try (Connection connection = ConnectionManagerWithPool.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, ticket.getPassengerNo());
            preparedStatement.setString(2, ticket.getPassengerName());
            preparedStatement.setLong(3, ticket.getFlight().id());
            preparedStatement.setString(4, ticket.getSeatNo());
            preparedStatement.setBigDecimal(5, ticket.getCost());
            preparedStatement.setLong(6, ticket.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DaoExceptions(throwables);
        }
    }

    public TicketEntity saveTicket(TicketEntity ticket) {
        try (Connection connection = ConnectionManagerWithPool.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, ticket.getPassengerNo());
            preparedStatement.setString(2, ticket.getPassengerName());
            preparedStatement.setLong(3, ticket.getFlight().id());
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
