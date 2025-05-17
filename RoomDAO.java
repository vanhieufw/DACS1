package com.movie.dao;

import com.movie.model.Room;
import com.movie.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RoomDAO {
    public void addRoom(Room room) throws SQLException {
        String query = "INSERT INTO Room (RoomName, Capacity) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, room.getRoomName());
            stmt.setInt(2, room.getCapacity());
            stmt.executeUpdate();
        }
    }
}