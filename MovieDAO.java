package com.movie.dao;

import com.movie.model.Movie;
import com.movie.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {
    public void addMovie(Movie movie) throws SQLException {
        String query = "INSERT INTO Movie (Title, Description, Poster) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, movie.getTitle());
            stmt.setString(2, movie.getDescription());
            stmt.setString(3, movie.getPoster());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    movie.setMovieID(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to add movie: " + e.getMessage(), e);
        }
    }

    public List<Movie> getAllMovies() throws SQLException {
        List<Movie> movies = new ArrayList<>();
        String query = "SELECT * FROM Movie";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Movie movie = new Movie();
                movie.setMovieID(rs.getInt("MovieID"));
                movie.setTitle(rs.getString("Title"));
                movie.setDescription(rs.getString("Description"));
                movie.setPoster(rs.getString("Poster"));
                movies.add(movie);
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to retrieve movies: " + e.getMessage(), e);
        }
        return movies;
    }

    public Movie getMovieById(int movieID) throws SQLException {
        String query = "SELECT * FROM Movie WHERE MovieID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, movieID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Movie movie = new Movie();
                    movie.setMovieID(rs.getInt("MovieID"));
                    movie.setTitle(rs.getString("Title"));
                    movie.setDescription(rs.getString("Description"));
                    movie.setPoster(rs.getString("Poster"));
                    return movie;
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to retrieve movie with ID " + movieID + ": " + e.getMessage(), e);
        }
        return null;
    }

    public void updateMovie(Movie movie) throws SQLException {
        String query = "UPDATE Movie SET Title = ?, Description = ?, Poster = ? WHERE MovieID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, movie.getTitle());
            stmt.setString(2, movie.getDescription());
            stmt.setString(3, movie.getPoster());
            stmt.setInt(4, movie.getMovieID());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No movie found with ID " + movie.getMovieID());
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to update movie: " + e.getMessage(), e);
        }
    }

    public void deleteMovie(int movieID) throws SQLException {
        String query = "DELETE FROM Movie WHERE MovieID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, movieID);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No movie found with ID " + movieID);
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to delete movie: " + e.getMessage(), e);
        }
    }
}