package com.movie.ui;

import com.movie.bus.MovieBUS;
import com.movie.model.Movie;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class AdminFrame extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private MovieBUS movieBUS = new MovieBUS();
    private JTable movieTable;
    private DefaultTableModel tableModel;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JTextField posterField;
    private JLabel posterPreview;
    private JButton updateButton;
    private JButton deleteButton;

    public AdminFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Quản lý bán vé xem phim - Hiếu");
        setSize(1000, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        JPanel mainView = createMainView();
        mainPanel.add(mainView, "MainView");

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createMainView() {
        JPanel mainView = new JPanel(new BorderLayout());
        mainView.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(40, 40, 40));

        JButton infoButton = new JButton("Thông tin phim");
        JButton scheduleButton = new JButton("Lịch chiếu");
        JButton ticketsButton = new JButton("Vé đã đặt");
        JButton statsButton = new JButton("Thống kê");
        JButton logoutButton = new JButton("Đăng xuất");

        styleButton(infoButton);
        styleButton(scheduleButton);
        styleButton(ticketsButton);
        styleButton(statsButton);
        styleButton(logoutButton);

        infoButton.addActionListener(e -> showPanel("Thông tin phim"));
        scheduleButton.addActionListener(e -> showPanel("Lịch chiếu"));
        ticketsButton.addActionListener(e -> showPanel("Vé đã đặt"));
        statsButton.addActionListener(e -> showPanel("Thống kê"));
        logoutButton.addActionListener(e -> {
            dispose();
            // new LoginFrame().setVisible(true); // Comment out as LoginFrame is not provided
        });

        sidebar.add(Box.createVerticalStrut(30));
        sidebar.add(infoButton);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(scheduleButton);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(ticketsButton);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(statsButton);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(logoutButton);

        // Content area
        contentPanel = new JPanel(new CardLayout());
        contentPanel.add(createInfoPanel(), "Thông tin phim");
        contentPanel.add(createSchedulePanel(), "Lịch chiếu");
        contentPanel.add(createTicketsPanel(), "Vé đã đặt");
        contentPanel.add(createStatsPanel(), "Thống kê");

        mainView.add(sidebar, BorderLayout.WEST);
        mainView.add(contentPanel, BorderLayout.CENTER);

        return mainView;
    }

    private void styleButton(JButton button) {
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setBackground(new Color(60, 60, 60));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(80, 80, 80));
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(60, 60, 60));
            }
        });
    }

    private void showPanel(String panelName) {
        if (contentPanel != null) {
            CardLayout cl = (CardLayout) contentPanel.getLayout();
            cl.show(contentPanel, panelName);
        } else {
            System.err.println("Lỗi: contentPanel chưa được khởi tạo!");
        }
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        JLabel titleLabel = new JLabel("Thông tin phim", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Main content
        JPanel mainContent = new JPanel(new BorderLayout(10, 10));
        mainContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form panel (initially hidden)
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Title:"), gbc);
        titleField = new JTextField(20);
        titleField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(titleField, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Description:"), gbc);
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2;
        formPanel.add(new JScrollPane(descriptionArea), gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Poster:"), gbc);
        posterField = new JTextField(20);
        posterField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(posterField, gbc);
        JButton choosePosterButton = new JButton("Choose Image");
        choosePosterButton.setEnabled(false);
        gbc.gridx = 2; gbc.gridy = 2;
        formPanel.add(choosePosterButton, gbc);

        posterPreview = new JLabel();
        posterPreview.setPreferredSize(new Dimension(150, 200));
        posterPreview.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        gbc.gridx = 3; gbc.gridy = 0; gbc.gridheight = 3; gbc.gridwidth = 1;
        formPanel.add(posterPreview, gbc);

        formPanel.setVisible(false);
        mainContent.add(formPanel, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"ID", "Title", "Description", "Poster"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 3 ? ImageIcon.class : String.class;
            }
        };
        movieTable = new JTable(tableModel);
        // Enforce single selection to ensure only one movie can be selected
        movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        movieTable.setRowHeight(100);
        movieTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        movieTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        movieTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        movieTable.getTableHeader().setBackground(new Color(60, 60, 60));
        movieTable.getTableHeader().setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(movieTable);
        mainContent.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Thêm phim");
        buttonPanel.add(addButton);
        updateButton = new JButton("Cập nhật");
        updateButton.setEnabled(false);
        deleteButton = new JButton("Xóa");
        deleteButton.setEnabled(false);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        mainContent.add(buttonPanel, BorderLayout.SOUTH);

        // Load initial data
        loadMovies();

        // Event listeners
        addButton.addActionListener(e -> showAddMovieDialog());
        movieTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = movieTable.getSelectedRow();
            updateButton.setEnabled(selectedRow != -1);
            deleteButton.setEnabled(selectedRow != -1);
            choosePosterButton.setEnabled(selectedRow != -1);
            titleField.setEditable(selectedRow != -1);
            descriptionArea.setEditable(selectedRow != -1);
            if (selectedRow != -1) {
                formPanel.setVisible(true);
                loadSelectedMovie();
            } else {
                formPanel.setVisible(false);
                clearForm();
            }
        });
        updateButton.addActionListener(e -> updateMovie());
        deleteButton.addActionListener(e -> deleteMovie());
        choosePosterButton.addActionListener(e -> choosePoster());

        panel.add(mainContent, BorderLayout.CENTER);
        return panel;
    }

    private void choosePoster() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String posterPath = selectedFile.getAbsolutePath();
            posterField.setText(posterPath);
            posterPreview.setIcon(new ImageIcon(new ImageIcon(posterPath).getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH)));
        }
    }

    private void showAddMovieDialog() {
        JDialog dialog = new JDialog(this, "Thêm phim", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setBackground(new Color(245, 245, 245));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Title:"), gbc);
        JTextField tempTitleField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0;
        inputPanel.add(tempTitleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Description:"), gbc);
        JTextArea tempDescriptionArea = new JTextArea(3, 20);
        tempDescriptionArea.setLineWrap(true);
        gbc.gridx = 1; gbc.gridy = 1;
        inputPanel.add(new JScrollPane(tempDescriptionArea), gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Poster:"), gbc);
        JTextField tempPosterField = new JTextField(20);
        tempPosterField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 2;
        inputPanel.add(tempPosterField, gbc);
        JButton choosePosterButton = new JButton("Choose Image");
        gbc.gridx = 2; gbc.gridy = 2;
        inputPanel.add(choosePosterButton, gbc);

        JLabel tempPosterPreview = new JLabel();
        tempPosterPreview.setPreferredSize(new Dimension(150, 200));
        tempPosterPreview.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        gbc.gridx = 3; gbc.gridy = 0; gbc.gridheight = 3;
        inputPanel.add(tempPosterPreview, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        choosePosterButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif"));
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String posterPath = selectedFile.getAbsolutePath();
                tempPosterField.setText(posterPath);
                tempPosterPreview.setIcon(new ImageIcon(new ImageIcon(posterPath).getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH)));
            }
        });
        saveButton.addActionListener(e -> {
            try {
                String title = tempTitleField.getText().trim();
                String description = tempDescriptionArea.getText().trim();
                String poster = tempPosterField.getText().trim();

                if (title.isEmpty()) {
                    throw new IllegalArgumentException("Title cannot be empty");
                }
                if (description.isEmpty()) {
                    throw new IllegalArgumentException("Description cannot be empty");
                }
                if (!poster.isEmpty() && !poster.matches(".*\\.(jpg|jpeg|png|gif)$")) {
                    throw new IllegalArgumentException("Invalid poster format");
                }

                movieBUS.addMovie(title, description, poster);
                JOptionPane.showMessageDialog(this, "Movie added successfully");
                clearForm();
                loadMovies();
                dialog.dispose();
            } catch (SQLException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Failed to add movie: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void updateMovie() {
        try {
            int selectedRow = movieTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a movie to update", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String title = titleField.getText().trim();
            String description = descriptionArea.getText().trim();
            String poster = posterField.getText().trim();

            if (title.isEmpty()) {
                throw new IllegalArgumentException("Title cannot be empty");
            }
            if (description.isEmpty()) {
                throw new IllegalArgumentException("Description cannot be empty");
            }
            if (!poster.isEmpty() && !poster.matches(".*\\.(jpg|jpeg|png|gif)$")) {
                throw new IllegalArgumentException("Invalid poster format");
            }

            int movieID = (int) tableModel.getValueAt(selectedRow, 0);
            movieBUS.updateMovie(movieID, title, description, poster);
            JOptionPane.showMessageDialog(this, "Movie updated successfully");
            clearForm();
            loadMovies();
        } catch (SQLException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Failed to update movie: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMovie() {
        try {
            int selectedRow = movieTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a movie to delete", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int movieID = (int) tableModel.getValueAt(selectedRow, 0);
            if (JOptionPane.showConfirmDialog(this, "Are you sure to delete this movie?", "Confirm Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                movieBUS.deleteMovie(movieID);
                JOptionPane.showMessageDialog(this, "Movie deleted successfully");
                clearForm();
                loadMovies();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete movie: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSelectedMovie() {
        int selectedRow = movieTable.getSelectedRow();
        if (selectedRow != -1) {
            titleField.setText((String) tableModel.getValueAt(selectedRow, 1));
            descriptionArea.setText((String) tableModel.getValueAt(selectedRow, 2));
            String poster = (String) tableModel.getValueAt(selectedRow, 3);
            posterField.setText(poster);
            posterPreview.setIcon(poster.isEmpty() ? null : new ImageIcon(new ImageIcon(poster).getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH)));
        }
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
                        icon != null ? icon : ""
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load movies: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        titleField.setText("");
        descriptionArea.setText("");
        posterField.setText("");
        posterPreview.setIcon(null);
        titleField.setEditable(false);
        descriptionArea.setEditable(false);
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private JPanel createSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.add(new JLabel("Lịch chiếu", SwingConstants.CENTER), BorderLayout.NORTH);
        JTextArea scheduleText = new JTextArea("Lịch chiếu:\n- Phim A: 14:00, Phòng 1\n- Phim B: 16:00, Phòng 2\n- Phim C: 18:00, Phòng 1");
        scheduleText.setEditable(false);
        scheduleText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        scheduleText.setBackground(new Color(255, 255, 255));
        panel.add(new JScrollPane(scheduleText), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTicketsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.add(new JLabel("Vé đã đặt", SwingConstants.CENTER), BorderLayout.NORTH);
        JTextArea ticketsText = new JTextArea("Vé đã đặt:\n- Khách hàng 1: Phim A, Ghế A1\n- Khách hàng 2: Phim B, Ghế B2");
        ticketsText.setEditable(false);
        ticketsText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ticketsText.setBackground(new Color(255, 255, 255));
        panel.add(new JScrollPane(ticketsText), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.add(new JLabel("Thống kê", SwingConstants.CENTER), BorderLayout.NORTH);
        JTextArea statsText = new JTextArea("Thống kê:\n- Tổng vé bán: 50\n- Doanh thu: 5,000,000 VND");
        statsText.setEditable(false);
        statsText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statsText.setBackground(new Color(255, 255, 255));
        panel.add(new JScrollPane(statsText), BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminFrame().setVisible(true));
    }
}
