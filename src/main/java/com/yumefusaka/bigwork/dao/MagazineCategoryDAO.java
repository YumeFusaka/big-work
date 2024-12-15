package com.yumefusaka.bigwork.dao;

import com.yumefusaka.bigwork.model.MagazineCategory;
import com.yumefusaka.bigwork.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MagazineCategoryDAO {
    
    public List<MagazineCategory> findAll() throws SQLException {
        List<MagazineCategory> categories = new ArrayList<>();
        String sql = "SELECT category_id, name FROM magazinecategory";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                MagazineCategory category = new MagazineCategory();
                category.setId(rs.getInt("category_id"));
                category.setName(rs.getString("name"));
                categories.add(category);
            }
        }
        return categories;
    }
    
    public List<MagazineCategory> search(String keyword) throws SQLException {
        List<MagazineCategory> categories = new ArrayList<>();
        String sql = "SELECT category_id, name FROM magazinecategory WHERE name LIKE ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                MagazineCategory category = new MagazineCategory();
                category.setId(rs.getInt("category_id"));
                category.setName(rs.getString("name"));
                categories.add(category);
            }
        }
        return categories;
    }
    
    public void insert(MagazineCategory category) throws SQLException {
        String sql = "INSERT INTO magazinecategory (name) VALUES (?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, category.getName());
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                category.setId(rs.getInt(1));
            }
        }
    }
    
    public void update(MagazineCategory category) throws SQLException {
        String sql = "UPDATE magazinecategory SET name = ? WHERE category_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category.getName());
            pstmt.setInt(2, category.getId());
            pstmt.executeUpdate();
        }
    }
    
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM magazinecategory WHERE category_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
    
    public MagazineCategory findById(int id) throws SQLException {
        String sql = "SELECT category_id, name FROM magazinecategory WHERE category_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                MagazineCategory category = new MagazineCategory();
                category.setId(rs.getInt("category_id"));
                category.setName(rs.getString("name"));
                return category;
            }
        }
        return null;
    }
} 