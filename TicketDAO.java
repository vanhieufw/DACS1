package com.movie.dao;

import com.movie.model.Ticket;
import com.movie.model.BookingHistory;
import com.movie.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {
    public void bookTicket(Ticket ticket) throws SQLException {
        String query = "INSERT INTO Ticket (CustomerID, ShowtimeID, SeatID, Price) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch
            try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, ticket.getCustomerID());
                stmt.setInt(2, ticket.getShowtimeID());
                stmt.setInt(3, ticket.getSeatID());
                stmt.setDouble(4, ticket.getPrice());
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        ticket.setTicketID(rs.getInt(1));
                        // Cập nhật trạng thái ghế
                        updateSeatStatus(ticket.getSeatID(), "Đã đặt");
                        // Thêm vào lịch sử đặt vé
                        addBookingHistory(ticket);
                    }
                }
                conn.commit(); // Commit giao dịch
            }
        } catch (SQLException e) {
            if (conn != null) conn.rollback(); // Rollback nếu có lỗi
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true); // Khôi phục auto-commit
            if (conn != null) conn.close();
        }
    }

    private void updateSeatStatus(int seatID, String status) throws SQLException {
        String query = "UPDATE Seat SET Status = ? WHERE SeatID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, seatID);
            stmt.executeUpdate();
        }
    }

    private void addBookingHistory(Ticket ticket) throws SQLException {
        String query = "INSERT INTO BookingHistory (CustomerID, TicketID, BookingDate, MovieTitle, RoomName, SeatNumber, Price) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            // Lấy thông tin phim, phòng, ghế
            String infoQuery = "SELECT m.Title, r.RoomName, s.SeatNumber " +
                    "FROM Ticket t " +
                    "JOIN Showtime st ON t.ShowtimeID = st.ShowtimeID " +
                    "JOIN Movie m ON st.MovieID = m.MovieID " +
                    "JOIN Room r ON st.RoomID = r.RoomID " +
                    "JOIN Seat s ON t.SeatID = s.SeatID " +
                    "WHERE t.TicketID = ?";
            try (PreparedStatement infoStmt = conn.prepareStatement(infoQuery)) {
                infoStmt.setInt(1, ticket.getTicketID());
                ResultSet rs = infoStmt.executeQuery();
                if (rs.next()) {
                    stmt.setInt(1, ticket.getCustomerID());
                    stmt.setInt(2, ticket.getTicketID());
                    stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                    stmt.setString(4, rs.getString("Title"));
                    stmt.setString(5, rs.getString("RoomName"));
                    stmt.setString(6, rs.getString("SeatNumber"));
                    stmt.setDouble(7, ticket.getPrice());
                    stmt.executeUpdate();
                }
            }
        }
    }

    public List<BookingHistory> getBookingHistory(int customerID) throws SQLException { // Đổi tên để khớp với TicketBUS
        List<BookingHistory> history = new ArrayList<>();
        String query = "SELECT * FROM BookingHistory WHERE CustomerID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BookingHistory bh = new BookingHistory();
                    bh.setHistoryID(rs.getInt("HistoryID"));
                    bh.setCustomerID(rs.getInt("CustomerID"));
                    bh.setTicketID(rs.getInt("TicketID"));
                    bh.setBookingDate(rs.getTimestamp("BookingDate"));
                    bh.setMovieTitle(rs.getString("MovieTitle"));
                    bh.setRoomName(rs.getString("RoomName"));
                    bh.setSeatNumber(rs.getString("SeatNumber"));
                    bh.setPrice(rs.getDouble("Price"));
                    history.add(bh);
                }
            }
        }
        return history;
    }
}
