package com.movie.ui;

import javax.swing.*;
import com.movie.bus.TicketBUS;
import com.movie.model.Seat;
import java.util.ArrayList;
import java.util.List;

public class BookingFrame extends JFrame {
    private final TicketBUS ticketBUS = new TicketBUS();
    private final List<Seat> selectedSeats = new ArrayList<>(); // To store selected seats

    public BookingFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Đặt vé");
        setSize(600, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel();
        JButton selectSeatButton = new JButton("Chọn ghế");
        JButton payButton = new JButton("Thanh toán");

        selectSeatButton.addActionListener(e -> {
            String seatNumber = JOptionPane.showInputDialog("Chọn ghế (VD: A1):");
            if (seatNumber != null && !seatNumber.isEmpty()) {
                // Create a Seat object and add to the list
                Seat seat = new Seat(seatNumber);
                selectedSeats.add(seat);
                JOptionPane.showMessageDialog(null, "Ghế " + seatNumber + " đã được chọn!");
            }
        });

        payButton.addActionListener(e -> {
            if (selectedSeats.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn ít nhất một ghế trước khi thanh toán!");
                return;
            }
            // Call processPayment with correct parameters
            String payment = ticketBUS.processPayment(1, 1, selectedSeats, 100000.0); // Example customerId=1, showtimeId=1
            JOptionPane.showMessageDialog(null, payment);
        });

        contentPane.add(selectSeatButton);
        contentPane.add(payButton);

        setContentPane(contentPane);
    }
}
