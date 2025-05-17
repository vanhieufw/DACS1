package com.movie.ui;

import com.movie.bus.MovieBUS;
import com.movie.bus.TicketBUS;
import com.movie.model.Movie;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class UserFrame extends JFrame {
    private MovieBUS movieBUS = new MovieBUS();
    private TicketBUS ticketBUS = new TicketBUS();
    private JTable movieTable;
    private DefaultTableModel tableModel;

    public UserFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Người dùng - Quản lý phim");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Danh sách phim", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        contentPane.add(titleLabel, BorderLayout.NORTH);

        // Movie table
        String[] columnNames = {"ID", "Title", "Description", "Poster"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 3 ? ImageIcon.class : super.getColumnClass(columnIndex);
            }
        };
        movieTable = new JTable(tableModel);
        movieTable.setRowHeight(100);
        movieTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        movieTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        movieTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        movieTable.getTableHeader().setBackground(new Color(60, 60, 60));
        movieTable.getTableHeader().setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(movieTable);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton viewDetailsButton = new JButton("Xem chi tiết");
        JButton bookTicketButton = new JButton("Đặt vé");
        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(bookTicketButton);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        // Event listeners
        viewDetailsButton.addActionListener(e -> viewMovieDetails());
        bookTicketButton.addActionListener(e -> {
            new BookingFrame().setVisible(true); // Giả định BookingFrame đã có
        });

        // Load movies
        loadMovies();

        setContentPane(contentPane);
        setVisible(true);
    }

    private void loadMovies() {
        try {
            List<Movie> movies = movieBUS.getAllMovies();
            tableModel.setRowCount(0);
            for (Movie movie : movies) {
                ImageIcon icon = movie.getPoster().isEmpty() ? null :
                        new ImageIcon(new ImageIcon(movie.getPoster()).getImage().getScaledInstance(80, 100, Image.SCALE_SMOOTH));
                tableModel.addRow(new Object[]{
                        movie.getMovieID(),
                        movie.getTitle(),
                        movie.getDescription(),
                        icon
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load movies: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewMovieDetails() {
        int selectedRow = movieTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a movie to view details", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            int movieID = (int) tableModel.getValueAt(selectedRow, 0);
            Movie movie = movieBUS.getMovieById(movieID);
            if (movie != null) {
                JPanel detailPanel = new JPanel(new BorderLayout());
                JLabel titleLabel = new JLabel(movie.getTitle(), SwingConstants.CENTER);
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                JTextArea descriptionArea = new JTextArea(movie.getDescription());
                descriptionArea.setEditable(false);
                descriptionArea.setLineWrap(true);
                descriptionArea.setWrapStyleWord(true);
                descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                JLabel posterLabel = new JLabel();
                if (!movie.getPoster().isEmpty()) {
                    posterLabel.setIcon(new ImageIcon(new ImageIcon(movie.getPoster()).getImage().getScaledInstance(200, 300, Image.SCALE_SMOOTH)));
                }
                detailPanel.add(titleLabel, BorderLayout.NORTH);
                detailPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);
                detailPanel.add(posterLabel, BorderLayout.WEST);
                JOptionPane.showMessageDialog(this, detailPanel, "Movie Details", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load movie details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserFrame().setVisible(true));
    }
}