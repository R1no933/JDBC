package jdbc;

import jdbc.dao.TicketDao;
import jdbc.entity.TicketEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class DaoRunner {
    public static void main(String[] args) {

    }

    private static void testGetAll() {
        TicketDao ticketDao = TicketDao.getInstance();
        List<TicketEntity> allTickets = ticketDao.findAll();
        System.out.println();
    }

    private static void testUpdate() {
        TicketDao ticketDao = TicketDao.getInstance();
        Optional<TicketEntity> ticketById = ticketDao.findTicketById(2L);
        System.out.println(ticketById);

        ticketById.ifPresent(ticket -> {
            ticket.setCost(BigDecimal.valueOf(288.88));
            ticketDao.updateTicket(ticket);
        });

        System.out.println(ticketById);
    }

    private static void testSave() {
        TicketDao ticketDao = TicketDao.getInstance();
        var ticket = new TicketEntity();

        ticket.setPassengerNo("44556677");
        ticket.setPassengerName("Test Passenger");
        ticket.setFlightId(3L);
        ticket.setSeatNo("22A");
        ticket.setCost(BigDecimal.TEN);
        System.out.println(ticketDao.saveTicket(ticket));
    }
}
