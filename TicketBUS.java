package com.movie.bus;

import com.movie.dao.TicketDAO;
import com.movie.model.Ticket;
import com.movie.util.SocketClient;
import java.sql.SQLException;

public class TicketBUS {
    private TicketDAO ticketDAO = new TicketDAO();

    public void selectSeat(String seat) {
        System.out.println("Selected seat: " + seat);
        SocketClient.sendMessage("Seat selected: " + seat);
    }

    public String processPayment(String amount) {
        try {
            Ticket ticket = new Ticket();
            ticket.setCustomerID(1);
            ticket.setShowtimeID(1);
            ticket.setSeat("A1");
            ticket.setPrice(Double.parseDouble(amount));
            ticketDAO.bookTicket(ticket);
            SocketClient.sendMessage("Ticket booked: Seat A1, Amount: " + amount);
            return "Thanh toán thành công: " + amount + " VND";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Thanh toán thất bại";
        }
    }
}