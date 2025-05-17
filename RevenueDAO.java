package com.movie.dao;

import com.movie.model.Revenue;
import com.movie.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RevenueDAO {
    public double getTotalRevenue() throws SQLException {
        String query = "SELECT SUM(TotalRevenue) as Total FROM Revenue";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("Total");
            }
        }
        return 0.0;
    }
}