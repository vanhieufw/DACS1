package com.movie.ui;

import com.movie.bus.MovieBUS;
import com.movie.model.Movie;
import com.movie.dao.MovieDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class AdminFrame extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private MovieBUS movieBUS = new MovieBUS();
    private MovieDAO movieDAO = new MovieDAO();
    private JPanel movieListPanel;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JTextField durationField;
    private JTextField directorField;
    private JTextField genreField;
    private JTextField posterField;
    private JLabel posterPreview;
    private JTextField startDateField;
    private JTextField endDateField;
    private JTextField productionYearField;
    private JTextField countryField;
    private JButton updateButton;
    private JButton deleteButton;
    private JPanel formPanel;

    public AdminFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Quản lý bán vé xem phim - Hiếu");
        setSize(1200, 800);
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

        JButton homeButton = new JButton("Trang chủ");
        JButton infoButton = new JButton("Thông tin phim");
        JButton roomButton = new JButton("Phòng chiếu");
        JButton staffButton = new JButton("Nhân viên");
        JButton customerButton = new JButton("Khách hàng");
        JButton statsButton = new JButton("Thống kê");
        JButton logoutButton = new JButton("Đăng xuất");

        styleButton(homeButton);
        styleButton(infoButton);
        styleButton(roomButton);
        styleButton(staffButton);
        styleButton(customerButton);
        styleButton(statsButton);
        styleButton(logoutButton);

        homeButton.addActionListener(e -> showPanel("Trang chủ"));
        infoButton.addActionListener(e -> showPanel("Thông tin phim"));
        roomButton.addActionListener(e -> showPanel("Phòng chiếu"));
        staffButton.addActionListener(e -> showPanel("Nhân viên"));
        customerButton.addActionListener(e -> showPanel("Khách hàng"));
        statsButton.addActionListener(e -> showPanel("Thống kê"));
        logoutButton.addActionListener(e -> dispose());

        sidebar.add(Box.createVerticalStrut(30));
        sidebar.add(homeButton);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(infoButton);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(roomButton);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(staffButton);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(customerButton);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(statsButton);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(logoutButton);

        // Content area
        contentPanel = new JPanel(new CardLayout());
        contentPanel.add(createHomePanel(), "Trang chủ");
        contentPanel.add(createInfoPanel(), "Thông tin phim");
        contentPanel.add(createRoomPanel(), "Phòng chiếu");
        contentPanel.add(createStaffPanel(), "Nhân viên");
        contentPanel.add(createCustomerPanel(), "Khách hàng");
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

        // Movie list panel with posters
        movieListPanel = new JPanel();
        movieListPanel.setLayout(new BoxLayout(movieListPanel, BoxLayout.Y_AXIS));
        JScrollPane movieScrollPane = new JScrollPane(movieListPanel);
        mainContent.add(movieScrollPane, BorderLayout.CENTER);

        // Form panel (hidden until a movie is selected)
        formPanel = new JPanel(new GridBagLayout());
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
        formPanel.add(new JLabel("Duration (minutes):"), gbc);
        durationField = new JTextField(20);
        durationField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(durationField, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Director:"), gbc);
        directorField = new JTextField(20);
        directorField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(directorField, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Genre:"), gbc);
        genreField = new JTextField(20);
        genreField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(genreField, gbc);
        JButton selectGenreButton = new JButton("Select Genre");
        selectGenreButton.setEnabled(false);
        gbc.gridx = 2; gbc.gridy = 4;
        formPanel.add(selectGenreButton, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Poster:"), gbc);
        posterField = new JTextField(20);
        posterField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 5;
        formPanel.add(posterField, gbc);
        JButton choosePosterButton = new JButton("Choose Image");
        choosePosterButton.setEnabled(false);
        gbc.gridx = 2; gbc.gridy = 5;
        formPanel.add(choosePosterButton, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Start Date (yyyy-MM-dd):"), gbc);
        startDateField = new JTextField(20);
        startDateField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 6;
        formPanel.add(startDateField, gbc);
        JButton selectStartDateButton = new JButton("Select Date");
        selectStartDateButton.setEnabled(false);
        gbc.gridx = 2; gbc.gridy = 6;
        formPanel.add(selectStartDateButton, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("End Date (yyyy-MM-dd):"), gbc);
        endDateField = new JTextField(20);
        endDateField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 7;
        formPanel.add(endDateField, gbc);
        JButton selectEndDateButton = new JButton("Select Date");
        selectEndDateButton.setEnabled(false);
        gbc.gridx = 2; gbc.gridy = 7;
        formPanel.add(selectEndDateButton, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(new JLabel("Production Year:"), gbc);
        productionYearField = new JTextField(20);
        productionYearField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 8; gbc.gridwidth = 2;
        formPanel.add(productionYearField, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 9;
        formPanel.add(new JLabel("Country:"), gbc);
        countryField = new JTextField(20);
        countryField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 9;
        formPanel.add(countryField, gbc);
        JButton selectCountryButton = new JButton("Select Country");
        selectCountryButton.setEnabled(false);
        gbc.gridx = 2; gbc.gridy = 9;
        formPanel.add(selectCountryButton, gbc);

        posterPreview = new JLabel();
        posterPreview.setPreferredSize(new Dimension(150, 200));
        posterPreview.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        gbc.gridx = 3; gbc.gridy = 0; gbc.gridheight = 10; gbc.gridwidth = 1;
        formPanel.add(posterPreview, gbc);

        formPanel.setVisible(false);
        mainContent.add(formPanel, BorderLayout.NORTH);

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
        choosePosterButton.addActionListener(e -> choosePoster());
        selectGenreButton.addActionListener(e -> selectGenre());
        selectCountryButton.addActionListener(e -> selectCountry());
        selectStartDateButton.addActionListener(e -> selectDate(startDateField));
        selectEndDateButton.addActionListener(e -> selectDate(endDateField));
        updateButton.addActionListener(e -> updateMovie());
        deleteButton.addActionListener(e -> deleteMovie());

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

    private void selectGenre() {
        try {
            List<String> genres = movieBUS.getAllGenres();
            String selectedGenre = (String) JOptionPane.showInputDialog(
                    this, "Select Genre:", "Genre Selection",
                    JOptionPane.PLAIN_MESSAGE, null, genres.toArray(), genres.get(0));
            if (selectedGenre != null) {
                genreField.setText(selectedGenre);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load genres: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selectCountry() {
        try {
            List<String> countries = movieBUS.getAllCountries();
            String selectedCountry = (String) JOptionPane.showInputDialog(
                    this, "Select Country:", "Country Selection",
                    JOptionPane.PLAIN_MESSAGE, null, countries.toArray(), countries.get(0));
            if (selectedCountry != null) {
                countryField.setText(selectedCountry);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load countries: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selectDate(JTextField dateField) {
        JDialog dateDialog = new JDialog(this, "Select Date", true);
        dateDialog.setSize(300, 150);
        dateDialog.setLocationRelativeTo(this);
        dateDialog.setLayout(new BorderLayout());

        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateDialog.add(dateSpinner, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        dateDialog.add(buttonPanel, BorderLayout.SOUTH);

        okButton.addActionListener(e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dateField.setText(sdf.format(dateSpinner.getValue()));
            dateDialog.dispose();
        });
        cancelButton.addActionListener(e -> dateDialog.dispose());

        dateDialog.setVisible(true);
    }

    private void showAddMovieDialog() {
        JDialog dialog = new JDialog(this, "Thêm phim", true);
        dialog.setSize(600, 600);
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
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2;
        inputPanel.add(tempTitleField, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Description:"), gbc);
        JTextArea tempDescriptionArea = new JTextArea(3, 20);
        tempDescriptionArea.setLineWrap(true);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2;
        inputPanel.add(new JScrollPane(tempDescriptionArea), gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Duration (minutes):"), gbc);
        JTextField tempDurationField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2;
        inputPanel.add(tempDurationField, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(new JLabel("Director:"), gbc);
        JTextField tempDirectorField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2;
        inputPanel.add(tempDirectorField, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 4;
        inputPanel.add(new JLabel("Genre:"), gbc);
        JTextField tempGenreField = new JTextField(20);
        tempGenreField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 4;
        inputPanel.add(tempGenreField, gbc);
        JButton tempSelectGenreButton = new JButton("Select Genre");
        gbc.gridx = 2; gbc.gridy = 4;
        inputPanel.add(tempSelectGenreButton, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 5;
        inputPanel.add(new JLabel("Poster:"), gbc);
        JTextField tempPosterField = new JTextField(20);
        tempPosterField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 5;
        inputPanel.add(tempPosterField, gbc);
        JButton tempChoosePosterButton = new JButton("Choose Image");
        gbc.gridx = 2; gbc.gridy = 5;
        inputPanel.add(tempChoosePosterButton, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 6;
        inputPanel.add(new JLabel("Start Date (yyyy-MM-dd):"), gbc);
        JTextField tempStartDateField = new JTextField(20);
        tempStartDateField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 6;
        inputPanel.add(tempStartDateField, gbc);
        JButton tempSelectStartDateButton = new JButton("Select Date");
        gbc.gridx = 2; gbc.gridy = 6;
        inputPanel.add(tempSelectStartDateButton, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 7;
        inputPanel.add(new JLabel("End Date (yyyy-MM-dd):"), gbc);
        JTextField tempEndDateField = new JTextField(20);
        tempEndDateField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 7;
        inputPanel.add(tempEndDateField, gbc);
        JButton tempSelectEndDateButton = new JButton("Select Date");
        gbc.gridx = 2; gbc.gridy = 7;
        inputPanel.add(tempSelectEndDateButton, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 8;
        inputPanel.add(new JLabel("Production Year:"), gbc);
        JTextField tempProductionYearField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 8; gbc.gridwidth = 2;
        inputPanel.add(tempProductionYearField, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 9;
        inputPanel.add(new JLabel("Country:"), gbc);
        JTextField tempCountryField = new JTextField(20);
        tempCountryField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 9;
        inputPanel.add(tempCountryField, gbc);
        JButton tempSelectCountryButton = new JButton("Select Country");
        gbc.gridx = 2; gbc.gridy = 9;
        inputPanel.add(tempSelectCountryButton, gbc);

        JLabel tempPosterPreview = new JLabel();
        tempPosterPreview.setPreferredSize(new Dimension(150, 200));
        tempPosterPreview.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        gbc.gridx = 3; gbc.gridy = 0; gbc.gridheight = 10;
        inputPanel.add(tempPosterPreview, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        tempChoosePosterButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif"));
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String posterPath = selectedFile.getAbsolutePath();
                tempPosterField.setText(posterPath);
                tempPosterPreview.setIcon(new ImageIcon(new ImageIcon(posterPath).getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH)));
            }
        });

        tempSelectGenreButton.addActionListener(e -> {
            try {
                List<String> genres = movieBUS.getAllGenres();
                String selectedGenre = (String) JOptionPane.showInputDialog(
                        dialog, "Select Genre:", "Genre Selection",
                        JOptionPane.PLAIN_MESSAGE, null, genres.toArray(), genres.get(0));
                if (selectedGenre != null) {
                    tempGenreField.setText(selectedGenre);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Failed to load genres: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        tempSelectCountryButton.addActionListener(e -> {
            try {
                List<String> countries = movieBUS.getAllCountries();
                String selectedCountry = (String) JOptionPane.showInputDialog(
                        dialog, "Select Country:", "Country Selection",
                        JOptionPane.PLAIN_MESSAGE, null, countries.toArray(), countries.get(0));
                if (selectedCountry != null) {
                    tempCountryField.setText(selectedCountry);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Failed to load countries: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        tempSelectStartDateButton.addActionListener(e -> {
            JDialog dateDialog = new JDialog(dialog, "Select Start Date", true);
            dateDialog.setSize(300, 150);
            dateDialog.setLocationRelativeTo(dialog);
            dateDialog.setLayout(new BorderLayout());

            JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
            JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
            dateSpinner.setEditor(dateEditor);
            dateDialog.add(dateSpinner, BorderLayout.CENTER);

            JPanel dateButtonPanel = new JPanel(new FlowLayout());
            JButton okButton = new JButton("OK");
            JButton cancelDateButton = new JButton("Cancel");
            dateButtonPanel.add(okButton);
            dateButtonPanel.add(cancelDateButton);
            dateDialog.add(dateButtonPanel, BorderLayout.SOUTH);

            okButton.addActionListener(ev -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                tempStartDateField.setText(sdf.format(dateSpinner.getValue()));
                dateDialog.dispose();
            });
            cancelDateButton.addActionListener(ev -> dateDialog.dispose());

            dateDialog.setVisible(true);
        });

        tempSelectEndDateButton.addActionListener(e -> {
            JDialog dateDialog = new JDialog(dialog, "Select End Date", true);
            dateDialog.setSize(300, 150);
            dateDialog.setLocationRelativeTo(dialog);
            dateDialog.setLayout(new BorderLayout());

            JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
            JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
            dateSpinner.setEditor(dateEditor);
            dateDialog.add(dateSpinner, BorderLayout.CENTER);

            JPanel dateButtonPanel = new JPanel(new FlowLayout());
            JButton okButton = new JButton("OK");
            JButton cancelDateButton = new JButton("Cancel");
            dateButtonPanel.add(okButton);
            dateButtonPanel.add(cancelDateButton);
            dateDialog.add(dateButtonPanel, BorderLayout.SOUTH);

            okButton.addActionListener(ev -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                tempEndDateField.setText(sdf.format(dateSpinner.getValue()));
                dateDialog.dispose();
            });
            cancelDateButton.addActionListener(ev -> dateDialog.dispose());

            dateDialog.setVisible(true);
        });

        saveButton.addActionListener(e -> {
            try {
                Movie movie = new Movie();
                movie.setTitle(tempTitleField.getText().trim());
                movie.setDescription(tempDescriptionArea.getText().trim());
                String durationStr = tempDurationField.getText().trim();
                movie.setDuration(durationStr.isEmpty() ? 0 : Integer.parseInt(durationStr));
                movie.setDirector(tempDirectorField.getText().trim());
                String genreName = tempGenreField.getText().trim();
                movie.setGenreID(genreName.isEmpty() ? 0 : movieDAO.getGenreIdByName(genreName));
                movie.setPoster(tempPosterField.getText().trim());
                String startDateStr = tempStartDateField.getText().trim();
                movie.setStartDate(startDateStr.isEmpty() ? null : Date.valueOf(startDateStr));
                String endDateStr = tempEndDateField.getText().trim();
                movie.setEndDate(endDateStr.isEmpty() ? null : Date.valueOf(endDateStr));
                String prodYearStr = tempProductionYearField.getText().trim();
                movie.setProductionYear(prodYearStr.isEmpty() ? 0 : Integer.parseInt(prodYearStr));
                String countryName = tempCountryField.getText().trim();
                movie.setCountryID(countryName.isEmpty() ? 0 : movieDAO.getCountryIdByName(countryName));

                if (movie.getTitle().isEmpty()) {
                    throw new IllegalArgumentException("Title cannot be empty");
                }
                if (!movie.getPoster().isEmpty() && !movie.getPoster().matches(".*\\.(jpg|jpeg|png|gif)$")) {
                    throw new IllegalArgumentException("Invalid poster format");
                }

                movieBUS.addMovie(movie);
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
            Movie movie = new Movie();
            movie.setMovieID(Integer.parseInt(formPanel.getName()));
            movie.setTitle(titleField.getText().trim());
            movie.setDescription(descriptionArea.getText().trim());
            String durationStr = durationField.getText().trim();
            movie.setDuration(durationStr.isEmpty() ? 0 : Integer.parseInt(durationStr));
            movie.setDirector(directorField.getText().trim());
            String genreName = genreField.getText().trim();
            movie.setGenreID(genreName.isEmpty() ? 0 : movieDAO.getGenreIdByName(genreName));
            movie.setPoster(posterField.getText().trim());
            String startDateStr = startDateField.getText().trim();
            movie.setStartDate(startDateStr.isEmpty() ? null : Date.valueOf(startDateStr));
            String endDateStr = endDateField.getText().trim();
            movie.setEndDate(endDateStr.isEmpty() ? null : Date.valueOf(endDateStr));
            String prodYearStr = productionYearField.getText().trim();
            movie.setProductionYear(prodYearStr.isEmpty() ? 0 : Integer.parseInt(prodYearStr));
            String countryName = countryField.getText().trim();
            movie.setCountryID(countryName.isEmpty() ? 0 : movieDAO.getCountryIdByName(countryName));

            if (movie.getTitle().isEmpty()) {
                throw new IllegalArgumentException("Title cannot be empty");
            }
            if (!movie.getPoster().isEmpty() && !movie.getPoster().matches(".*\\.(jpg|jpeg|png|gif)$")) {
                throw new IllegalArgumentException("Invalid poster format");
            }

            movieBUS.updateMovie(movie);
            JOptionPane.showMessageDialog(this.getParent(), "Movie updated successfully");
            clearForm();
            loadMovies();
        } catch (SQLException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this.getParent(), "Failed to update movie: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMovie() {
        try {
            int movieID = Integer.parseInt(formPanel.getName());
            if (JOptionPane.showConfirmDialog(this.getParent(), "Are you sure to delete this movie?", "Confirm Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                movieBUS.deleteMovie(movieID);
                JOptionPane.showMessageDialog(this.getParent(), "Movie deleted successfully");
                clearForm();
                loadMovies();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this.getParent(), "Failed to delete movie: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMovies() {
        try {
            movieListPanel.removeAll();
            List<Movie> movies = movieBUS.getAllMovies();
            for (Movie movie : movies) {
                JPanel moviePanel = new JPanel(new BorderLayout());
                moviePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                moviePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

                JLabel posterLabel = new JLabel();
                if (movie.getPoster() != null && !movie.getPoster().isEmpty()) {
                    posterLabel.setIcon(new ImageIcon(new ImageIcon(movie.getPoster()).getImage().getScaledInstance(100, 140, Image.SCALE_SMOOTH)));
                }
                posterLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                moviePanel.add(posterLabel, BorderLayout.WEST);

                JLabel titleLabel = new JLabel(movie.getTitle());
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                moviePanel.add(titleLabel, BorderLayout.CENTER);

                moviePanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        formPanel.setVisible(true);
                        formPanel.setName(String.valueOf(movie.getMovieID()));
                        titleField.setText(movie.getTitle());
                        descriptionArea.setText(movie.getDescription());
                        durationField.setText(String.valueOf(movie.getDuration()));
                        directorField.setText(movie.getDirector());
                        genreField.setText(movie.getGenreName());
                        posterField.setText(movie.getPoster());
                        posterPreview.setIcon(movie.getPoster() == null || movie.getPoster().isEmpty() ? null :
                                new ImageIcon(new ImageIcon(movie.getPoster()).getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH)));
                        startDateField.setText(movie.getStartDate() != null ? movie.getStartDate().toString() : "");
                        endDateField.setText(movie.getEndDate() != null ? movie.getEndDate().toString() : "");
                        productionYearField.setText(String.valueOf(movie.getProductionYear()));
                        countryField.setText(movie.getCountryName());
                        titleField.setEditable(true);
                        descriptionArea.setEditable(true);
                        durationField.setEditable(true);
                        directorField.setEditable(true);
                        updateButton.setEnabled(true);
                        deleteButton.setEnabled(true);
                        for (Component c : formPanel.getComponents()) {
                            if (c instanceof JButton) {
                                c.setEnabled(true);
                            }
                        }
                    }
                });

                movieListPanel.add(moviePanel);
            }
            movieListPanel.revalidate();
            movieListPanel.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load movies: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        formPanel.setVisible(false);
        formPanel.setName(null);
        titleField.setText("");
        descriptionArea.setText("");
        durationField.setText("");
        directorField.setText("");
        genreField.setText("");
        posterField.setText("");
        posterPreview.setIcon(null);
        startDateField.setText("");
        endDateField.setText("");
        productionYearField.setText("");
        countryField.setText("");
        titleField.setEditable(false);
        descriptionArea.setEditable(false);
        durationField.setEditable(false);
        directorField.setEditable(false);
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        for (Component c : formPanel.getComponents()) {
            if (c instanceof JButton) {
                c.setEnabled(false);
            }
        }
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.add(new JLabel("Trang chủ", SwingConstants.CENTER), BorderLayout.NORTH);
        JTextArea homeText = new JTextArea("Chào mừng đến với hệ thống quản lý bán vé xem phim!");
        homeText.setEditable(false);
        homeText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        homeText.setBackground(new Color(255, 255, 255));
        panel.add(new JScrollPane(homeText), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRoomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.add(new JLabel("Phòng chiếu", SwingConstants.CENTER), BorderLayout.NORTH);
        JTextArea roomText = new JTextArea("Danh sách phòng chiếu:\n- Phòng 1: 100 ghế\n- Phòng 2: 80 ghế");
        roomText.setEditable(false);
        roomText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roomText.setBackground(new Color(255, 255, 255));
        panel.add(new JScrollPane(roomText), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStaffPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.add(new JLabel("Nhân viên", SwingConstants.CENTER), BorderLayout.NORTH);
        JTextArea staffText = new JTextArea("Danh sách nhân viên:\n- Nhân viên 1: Quản lý\n- Nhân viên 2: Bán vé");
        staffText.setEditable(false);
        staffText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        staffText.setBackground(new Color(255, 255, 255));
        panel.add(new JScrollPane(staffText), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.add(new JLabel("Khách hàng", SwingConstants.CENTER), BorderLayout.NORTH);
        JTextArea customerText = new JTextArea("Danh sách khách hàng:\n- Khách hàng 1: VIP\n- Khách hàng 2: Thường");
        customerText.setEditable(false);
        customerText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        customerText.setBackground(new Color(255, 255, 255));
        panel.add(new JScrollPane(customerText), BorderLayout.CENTER);
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
