package jdbc.dao;

import jdbc.entity.FlightEntity;

import java.util.List;
import java.util.Optional;

public interface Dao<K, E> {
    boolean deleteTicket(K id);
    E saveTicket(E ticket);
    void updateTicket(E ticket);
    Optional<E> findTicketById(K id);
    List<E> findAll();
}
