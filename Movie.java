package com.movie.model;

import java.util.Objects;

public class Movie {
    private int movieID;
    private String title;
    private String description;
    private String poster;

    public Movie() {
    }

    public Movie(int movieID, String title, String description, String poster) {
        this.movieID = movieID;
        this.title = validateString(title, "Title");
        this.description = validateString(description, "Description");
        this.poster = validatePoster(poster);
    }

    public int getMovieID() {
        return movieID;
    }

    public void setMovieID(int movieID) {
        if (movieID < 0) {
            throw new IllegalArgumentException("Movie ID cannot be negative");
        }
        this.movieID = movieID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = validateString(title, "Title");
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = validateString(description, "Description");
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = validatePoster(poster);
    }

    private String validateString(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
        return value.trim();
    }

    private String validatePoster(String poster) {
        if (poster == null || poster.trim().isEmpty()) {
            return "";
        }
        if (!poster.matches(".*\\.(jpg|jpeg|png|gif)$")) {
            throw new IllegalArgumentException("Poster must be a valid image file path (jpg, jpeg, png, gif)");
        }
        return poster.trim();
    }

    @Override
    public String toString() {
        return "Movie{movieID=" + movieID + ", title='" + title + "', description='" + description + "', poster='" + poster + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return movieID == movie.movieID &&
                Objects.equals(title, movie.title) &&
                Objects.equals(description, movie.description) &&
                Objects.equals(poster, movie.poster);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieID, title, description, poster);
    }
}