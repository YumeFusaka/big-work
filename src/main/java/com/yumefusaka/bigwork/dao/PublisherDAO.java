package com.yumefusaka.bigwork.dao;

import com.yumefusaka.bigwork.model.Publisher;
import com.yumefusaka.bigwork.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PublisherDAO {
    
    public List<Publisher> findAll() throws SQLException {
        List<Publisher> publishers = new ArrayList<>();
        String sql = "SELECT publisher_id, name, address, contact FROM publisher";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Publisher publisher = new Publisher();
                publisher.setId(rs.getInt("publisher_id"));
                publisher.setName(rs.getString("name"));
                publisher.setAddress(rs.getString("address"));
                publisher.setContact(rs.getString("contact"));
                publishers.add(publisher);
            }
        }
        return publishers;
    }
    
    public List<Publisher> search(String keyword) throws SQLException {
        List<Publisher> publishers = new ArrayList<>();
        String sql = "SELECT publisher_id, name, address, contact FROM publisher WHERE name LIKE ? OR address LIKE ? OR contact LIKE ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Publisher publisher = new Publisher();
                publisher.setId(rs.getInt("publisher_id"));
                publisher.setName(rs.getString("name"));
                publisher.setAddress(rs.getString("address"));
                publisher.setContact(rs.getString("contact"));
                publishers.add(publisher);
            }
        }
        return publishers;
    }
    
    public void insert(Publisher publisher) throws SQLException {
        String sql = "INSERT INTO publisher (name, address, contact) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, publisher.getName());
            pstmt.setString(2, publisher.getAddress());
            pstmt.setString(3, publisher.getContact());
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                publisher.setId(rs.getInt(1));
            }
        }
    }
    
    public void update(Publisher publisher) throws SQLException {
        String sql = "UPDATE publisher SET name = ?, address = ?, contact = ? WHERE publisher_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, publisher.getName());
            pstmt.setString(2, publisher.getAddress());
            pstmt.setString(3, publisher.getContact());
            pstmt.setInt(4, publisher.getId());
            pstmt.executeUpdate();
        }
    }
    
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM publisher WHERE publisher_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
    
    public Publisher findById(int id) throws SQLException {
        String sql = "SELECT publisher_id, name, address, contact FROM publisher WHERE publisher_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Publisher publisher = new Publisher();
                publisher.setId(rs.getInt("publisher_id"));
                publisher.setName(rs.getString("name"));
                publisher.setAddress(rs.getString("address"));
                publisher.setContact(rs.getString("contact"));
                return publisher;
            }
        }
        return null;
    }
} 