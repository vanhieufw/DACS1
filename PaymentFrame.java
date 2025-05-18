package com.movie.ui;

import javax.swing.*;
import com.movie.bus.TicketBUS;
import com.movie.model.Seat;
import java.util.ArrayList;
import java.util.List;

public class PaymentFrame extends JFrame {
    private final TicketBUS ticketBUS = new TicketBUS();

    public PaymentFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Thanh toán");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel();
        JLabel amountLabel = new JLabel("Số tiền: 100000 VND");
        JButton confirmButton = new JButton("Xác nhận");

        confirmButton.addActionListener(e -> {
            // Create a list of seats with the new constructor
            List<Seat> seats = new ArrayList<>();
            seats.add(new Seat("A1")); // Now works with the new constructor
            seats.add(new Seat("A2"));

            // Call processPayment with assumed correct parameters
            String payment = ticketBUS.processPayment(1, 1, seats, 100000.0);
            JOptionPane.showMessageDialog(null, payment);
            dispose();
        });

        contentPane.add(amountLabel);
        contentPane.add(confirmButton);

        setContentPane(contentPane);
    }
}
