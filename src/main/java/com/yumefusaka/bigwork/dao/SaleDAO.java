package com.yumefusaka.bigwork.dao;

import com.yumefusaka.bigwork.model.Sale;
import com.yumefusaka.bigwork.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SaleDAO {
    private static final String INSERT = "INSERT INTO Sales (customer_id, item_type, item_id, quantity, sale_date, total_price) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_ALL = "SELECT s.*, c.name as customer_name, COALESCE(bk.title, m.title) as item_title FROM Sales s " +
            "LEFT JOIN Customer c ON s.customer_id = c.customer_id " +
            "LEFT JOIN Book bk ON s.item_type = 'BOOK' AND s.item_id = bk.book_id " +
            "LEFT JOIN Magazine m ON s.item_type = 'MAGAZINE' AND s.item_id = m.magazine_id";
    private static final String SELECT_BY_ID = SELECT_ALL + " WHERE s.sale_id = ?";
    private static final String SELECT_BY_CUSTOMER = SELECT_ALL + " WHERE s.customer_id = ?";

    public void insert(Sale sale) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, sale.getCustomerId());
            pstmt.setString(2, sale.getItemType());
            pstmt.setInt(3, sale.getItemId());
            pstmt.setInt(4, sale.getQuantity());
            pstmt.setDate(5, Date.valueOf(sale.getSaleDate()));
            pstmt.setDouble(6, sale.getTotalPrice());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    sale.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public List<Sale> findAll() throws SQLException {
        List<Sale> sales = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL)) {
            
            while (rs.next()) {
                sales.add(mapResultSetToSale(rs));
            }
        }
        return sales;
    }

    public Sale findById(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_ID)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSale(rs);
                }
            }
        }
        return null;
    }

    public List<Sale> findByCustomer(int customerId) throws SQLException {
        List<Sale> sales = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_CUSTOMER)) {
            
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sales.add(mapResultSetToSale(rs));
                }
            }
        }
        return sales;
    }

    private Sale mapResultSetToSale(ResultSet rs) throws SQLException {
        Sale sale = new Sale();
        sale.setId(rs.getInt("sale_id"));
        sale.setCustomerId(rs.getInt("customer_id"));
        sale.setCustomerName(rs.getString("customer_name"));
        sale.setItemType(rs.getString("item_type"));
        sale.setItemId(rs.getInt("item_id"));
        sale.setItemTitle(rs.getString("item_title"));
        sale.setQuantity(rs.getInt("quantity"));
        sale.setTotalPrice(rs.getDouble("total_price"));
        sale.setSaleDate(rs.getDate("sale_date").toLocalDate());
        return sale;
    }
} 