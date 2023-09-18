package jdbc.dto;

public record TicketEntityFilter(int limit,
                                 int offset,
                                 String passengerName,
                                 String seatNo) {

}
