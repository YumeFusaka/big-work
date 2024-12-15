package com.yumefusaka.bigwork.dao;

import com.yumefusaka.bigwork.model.Magazine;
import com.yumefusaka.bigwork.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MagazineDAO {
    private static final String INSERT = "INSERT INTO Magazine (title, publisher_id, category_id, total_quantity, available_quantity, price, unit) VALUES (?, ?, ?, ?, ?, ?, '本')";
    private static final String UPDATE = "UPDATE Magazine SET title=?, publisher_id=?, category_id=?, total_quantity=?, available_quantity=?, price=? WHERE magazine_id=?";
    private static final String DELETE = "DELETE FROM Magazine WHERE magazine_id=?";
    private static final String SELECT_ALL = "SELECT m.*, p.name as publisher_name, mc.name as category_name FROM Magazine m LEFT JOIN Publisher p ON m.publisher_id = p.publisher_id LEFT JOIN MagazineCategory mc ON m.category_id = mc.category_id";
    private static final String SELECT_BY_ID = SELECT_ALL + " WHERE m.magazine_id=?";
    private static final String SEARCH = SELECT_ALL + " WHERE m.title LIKE ? OR p.name LIKE ?";

    public void insert(Magazine magazine) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, magazine.getTitle());
            pstmt.setInt(2, magazine.getPublisherId());
            pstmt.setInt(3, magazine.getCategoryId());
            pstmt.setInt(4, magazine.getTotalQuantity());
            pstmt.setInt(5, magazine.getAvailableQuantity());
            pstmt.setDouble(6, magazine.getPrice());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    magazine.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void update(Magazine magazine) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE)) {
            
            pstmt.setString(1, magazine.getTitle());
            pstmt.setInt(2, magazine.getPublisherId());
            pstmt.setInt(3, magazine.getCategoryId());
            pstmt.setInt(4, magazine.getTotalQuantity());
            pstmt.setInt(5, magazine.getAvailableQuantity());
            pstmt.setDouble(6, magazine.getPrice());
            pstmt.setInt(7, magazine.getId());
            
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public List<Magazine> findAll() throws SQLException {
        List<Magazine> magazines = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL)) {
            
            while (rs.next()) {
                magazines.add(mapResultSetToMagazine(rs));
            }
        }
        return magazines;
    }

    public Magazine findById(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_ID)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMagazine(rs);
                }
            }
        }
        return null;
    }

    public List<Magazine> search(String keyword) throws SQLException {
        List<Magazine> magazines = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SEARCH)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    magazines.add(mapResultSetToMagazine(rs));
                }
            }
        }
        return magazines;
    }

    private Magazine mapResultSetToMagazine(ResultSet rs) throws SQLException {
        Magazine magazine = new Magazine();
        magazine.setId(rs.getInt("magazine_id"));
        magazine.setTitle(rs.getString("title"));
        magazine.setPublisherId(rs.getInt("publisher_id"));
        magazine.setCategoryId(rs.getInt("category_id"));
        magazine.setTotalQuantity(rs.getInt("total_quantity"));
        magazine.setAvailableQuantity(rs.getInt("available_quantity"));
        magazine.setPrice(rs.getDouble("price"));
        magazine.setPublisherName(rs.getString("publisher_name"));
        magazine.setCategoryName(rs.getString("category_name"));
        return magazine;
    }
} 