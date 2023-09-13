package jdbc;

import jdbc.dao.TicketDao;
import jdbc.entity.TicketEntity;

import java.math.BigDecimal;

public class DaoRunner {
    public static void main(String[] args) {
        TicketDao ticketDao = TicketDao.getInstance();
        System.out.println(ticketDao.deleteTicket(56L));
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
