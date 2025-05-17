package com.movie.model;

import java.util.Date;

public class Showtime {
    private int showtimeID;
    private int movieID;
    private int roomID;
    private Date showDate;

    public int getShowtimeID() { return showtimeID; }
    public void setShowtimeID(int showtimeID) { this.showtimeID = showtimeID; }
    public int getMovieID() { return movieID; }
    public void setMovieID(int movieID) { this.movieID = movieID; }
    public int getRoomID() { return roomID; }
    public void setRoomID(int roomID) { this.roomID = roomID; }
    public Date getShowDate() { return showDate; }
    public void setShowDate(Date showDate) { this.showDate = showDate; }
}