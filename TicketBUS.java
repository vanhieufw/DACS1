package com.movie.bus;

import com.movie.model.Ticket;
import com.movie.model.Seat;
import com.movie.model.BookingHistory;
import com.movie.dao.TicketDAO;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList; // Added import for ArrayList

// Giả định SocketClient tồn tại, nếu không cần tạo lớp này
import com.movie.util.SocketClient; // Thêm import cho SocketClient

public class TicketBUS {
    private final TicketDAO ticketDAO = new TicketDAO();

    public String processPayment(int customerID, int showtimeID, List<Seat> selectedSeats, double totalAmount) {
        try {
            for (Seat seat : selectedSeats) {
                Ticket ticket = new Ticket();
                ticket.setCustomerID(customerID);
                ticket.setShowtimeID(showtimeID);
                ticket.setSeatID(seat.getSeatID());
                ticket.setPrice(totalAmount / selectedSeats.size());
                ticketDAO.bookTicket(ticket);
                SocketClient.sendMessage("Ticket booked: Seat " + seat.getSeatNumber() + ", Amount: " + ticket.getPrice());
            }
            return "Thanh toán thành công: " + totalAmount + " VND";
        } catch (SQLException e) {
            e.printStackTrace(); // Nên thay bằng logging chuyên nghiệp
            return "Thanh toán thất bại: " + e.getMessage();
        }
    }

    public List<BookingHistory> getBookingHistory(int customerID) {
        try {
            return ticketDAO.getBookingHistory(customerID); // Sử dụng phương thức đã đổi tên
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>(); // Trả về danh sách rỗng nếu có lỗi
        }
    }
}
