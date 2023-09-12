package jdbc;

import jdbc.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionRunner {
    public static void main(String[] args) throws SQLException {

        long flightID = 8;
        String deleteFlightSQL = "DELETE FROM flight WHERE id = ?";
        String deleteTicketsSQL = "DELETE FROM ticket WHERE flight_id = ?";
        Connection connection = null;
        PreparedStatement deleteFlightStatment = null;
        PreparedStatement deleteTicketStatment = null;

        try {
            connection = ConnectionManager.openConncetion();
            deleteFlightStatment = connection.prepareStatement(deleteFlightSQL);
            deleteTicketStatment = connection.prepareStatement(deleteTicketsSQL);

            connection.setAutoCommit(false);

            deleteFlightStatment.setLong(1, flightID);
            deleteTicketStatment.setLong(1, flightID);

            deleteTicketStatment.executeUpdate();

            if (true) {
                throw new RuntimeException("Something wrong!");
            }

            deleteFlightStatment.executeUpdate();
            connection.commit();

        } catch (Exception error) {
            if (connection != null) {
                connection.rollback();
            }
            throw error;
        } finally {
            if (connection != null) {
                connection.close();
            }

            if (deleteFlightStatment != null) {
                deleteFlightStatment.close();
            }

            if (deleteTicketStatment != null) {
                deleteTicketStatment.close();
            }
        }
    }
}
