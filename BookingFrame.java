package com.movie.ui;

import javax.swing.*;
import com.movie.bus.TicketBUS;

public class BookingFrame extends JFrame {
    private TicketBUS ticketBUS = new TicketBUS();

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
            String seat = JOptionPane.showInputDialog("Chọn ghế (VD: A1):");
            if (seat != null && !seat.isEmpty()) {
                ticketBUS.selectSeat(seat);
                JOptionPane.showMessageDialog(null, "Ghế " + seat + " đã được chọn!");
            }
        });

        payButton.addActionListener(e -> {
            String amount = "100000"; // Giả lập giá vé
            String payment = ticketBUS.processPayment(amount);
            JOptionPane.showMessageDialog(null, payment);
        });

        contentPane.add(selectSeatButton);
        contentPane.add(payButton);

        setContentPane(contentPane);
    }
}