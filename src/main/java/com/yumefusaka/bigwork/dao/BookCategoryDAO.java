package com.yumefusaka.bigwork.dao;

import com.yumefusaka.bigwork.model.BookCategory;
import com.yumefusaka.bigwork.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookCategoryDAO {
    private static final String SELECT_ALL = "SELECT * FROM BookCategory";
    private static final String SELECT_BY_ID = "SELECT * FROM BookCategory WHERE category_id = ?";
    private static final String INSERT = "INSERT INTO BookCategory (name) VALUES (?)";
    private static final String UPDATE = "UPDATE BookCategory SET name = ? WHERE category_id = ?";
    private static final String DELETE = "DELETE FROM BookCategory WHERE category_id = ?";

    public List<BookCategory> findAll() throws SQLException {
        List<BookCategory> categories = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL)) {
            
            while (rs.next()) {
                BookCategory category = new BookCategory();
                category.setId(rs.getInt("category_id"));
                category.setName(rs.getString("name"));
                categories.add(category);
            }
        }
        return categories;
    }

    public BookCategory findById(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_ID)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BookCategory category = new BookCategory();
                    category.setId(rs.getInt("category_id"));
                    category.setName(rs.getString("name"));
                    return category;
                }
            }
        }
        return null;
    }

    public void insert(BookCategory category) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, category.getName());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    category.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void update(BookCategory category) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE)) {
            
            pstmt.setString(1, category.getName());
            pstmt.setInt(2, category.getId());
            
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
} 