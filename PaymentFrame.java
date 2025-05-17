package com.movie.ui;

import javax.swing.*;
import com.movie.bus.TicketBUS;

public class PaymentFrame extends JFrame {
    private TicketBUS ticketBUS = new TicketBUS();

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
            String payment = ticketBUS.processPayment("100000");
            JOptionPane.showMessageDialog(null, payment);
            dispose();
        });

        contentPane.add(amountLabel);
        contentPane.add(confirmButton);

        setContentPane(contentPane);
    }
}