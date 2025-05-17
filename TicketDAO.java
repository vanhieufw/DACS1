package com.movie.dao;

import com.movie.model.Ticket;
import com.movie.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TicketDAO {
    public void bookTicket(Ticket ticket) throws SQLException {
        String query = "INSERT INTO Ticket (CustomerID, ShowtimeID, Seat, Price) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, ticket.getCustomerID());
            stmt.setInt(2, ticket.getShowtimeID());
            stmt.setString(3, ticket.getSeat());
            stmt.setDouble(4, ticket.getPrice());
            stmt.executeUpdate();
        }
    }
}