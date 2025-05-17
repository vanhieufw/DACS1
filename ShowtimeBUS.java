package com.movie.bus;

import com.movie.dao.ShowtimeDAO;
import com.movie.model.Showtime;
import java.sql.SQLException;
import java.util.Date;

public class ShowtimeBUS {
    private ShowtimeDAO showtimeDAO = new ShowtimeDAO();

    public void updateShowtimes() {
        try {
            Showtime showtime = new Showtime();
            showtime.setMovieID(1);
            showtime.setRoomID(1);
            showtime.setShowDate(new Date());
            showtimeDAO.addShowtime(showtime);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}