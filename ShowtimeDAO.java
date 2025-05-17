package com.movie.dao;

import com.movie.model.Showtime;
import com.movie.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ShowtimeDAO {
    public void addShowtime(Showtime showtime) throws SQLException {
        String query = "INSERT INTO Showtime (MovieID, RoomID, ShowDate) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, showtime.getMovieID());
            stmt.setInt(2, showtime.getRoomID());
            stmt.setTimestamp(3, new java.sql.Timestamp(showtime.getShowDate().getTime()));
            stmt.executeUpdate();
        }
    }
}