package com.movie.bus;

import com.movie.dao.RoomDAO;
import com.movie.model.Room;
import java.sql.SQLException;

public class RoomBUS {
    private RoomDAO roomDAO = new RoomDAO();

    public void addRoom(String roomName, int capacity) throws SQLException {
        Room room = new Room();
        room.setRoomName(roomName);
        room.setCapacity(capacity);
        roomDAO.addRoom(room);
    }
}