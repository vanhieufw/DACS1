package com.movie.model;

public class Ticket {
    private int ticketID;
    private int customerID;
    private int showtimeID;
    private String seat;
    private double price;

    public int getTicketID() { return ticketID; }
    public void setTicketID(int ticketID) { this.ticketID = ticketID; }
    public int getCustomerID() { return customerID; }
    public void setCustomerID(int customerID) { this.customerID = customerID; }
    public int getShowtimeID() { return showtimeID; }
    public void setShowtimeID(int showtimeID) { this.showtimeID = showtimeID; }
    public String getSeat() { return seat; }
    public void setSeat(String seat) { this.seat = seat; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}