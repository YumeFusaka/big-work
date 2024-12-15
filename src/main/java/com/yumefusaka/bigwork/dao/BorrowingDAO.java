package com.yumefusaka.bigwork.dao;

import com.yumefusaka.bigwork.model.Borrowing;
import com.yumefusaka.bigwork.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowingDAO {
    private static final String INSERT = "INSERT INTO Borrowing (customer_id, item_type, item_id, borrow_date, status) VALUES (?, ?, ?, ?, 'BORROWED')";
    private static final String UPDATE = "UPDATE Borrowing SET return_date = ?, status = ? WHERE borrow_id = ?";
    private static final String SELECT_ALL = "SELECT b.*, c.name as customer_name, COALESCE(bk.title, m.title) as item_title FROM Borrowing b " +
            "LEFT JOIN Customer c ON b.customer_id = c.customer_id " +
            "LEFT JOIN Book bk ON b.item_type = 'BOOK' AND b.item_id = bk.book_id " +
            "LEFT JOIN Magazine m ON b.item_type = 'MAGAZINE' AND b.item_id = m.magazine_id";
    private static final String SELECT_BY_ID = SELECT_ALL + " WHERE b.borrow_id = ?";
    private static final String SELECT_BY_CUSTOMER = SELECT_ALL + " WHERE b.customer_id = ?";
    private static final String SELECT_ACTIVE = SELECT_ALL + " WHERE b.status = 'BORROWED'";

    public void insert(Borrowing borrowing) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, borrowing.getCustomerId());
            pstmt.setString(2, borrowing.getItemType());
            pstmt.setInt(3, borrowing.getItemId());
            pstmt.setDate(4, Date.valueOf(borrowing.getBorrowDate()));
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    borrowing.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void update(Borrowing borrowing) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE)) {
            
            pstmt.setDate(1, Date.valueOf(borrowing.getReturnDate()));
            pstmt.setString(2, borrowing.getStatus());
            pstmt.setInt(3, borrowing.getId());
            
            pstmt.executeUpdate();
        }
    }

    public List<Borrowing> findAll() throws SQLException {
        List<Borrowing> borrowings = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL)) {
            
            while (rs.next()) {
                borrowings.add(mapResultSetToBorrowing(rs));
            }
        }
        return borrowings;
    }

    public Borrowing findById(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_ID)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBorrowing(rs);
                }
            }
        }
        return null;
    }

    public List<Borrowing> findByCustomer(int customerId) throws SQLException {
        List<Borrowing> borrowings = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_CUSTOMER)) {
            
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    borrowings.add(mapResultSetToBorrowing(rs));
                }
            }
        }
        return borrowings;
    }

    public List<Borrowing> findActive() throws SQLException {
        List<Borrowing> borrowings = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ACTIVE)) {
            
            while (rs.next()) {
                borrowings.add(mapResultSetToBorrowing(rs));
            }
        }
        return borrowings;
    }

    private Borrowing mapResultSetToBorrowing(ResultSet rs) throws SQLException {
        Borrowing borrowing = new Borrowing();
        borrowing.setId(rs.getInt("borrow_id"));
        borrowing.setCustomerId(rs.getInt("customer_id"));
        borrowing.setCustomerName(rs.getString("customer_name"));
        borrowing.setItemType(rs.getString("item_type"));
        borrowing.setItemId(rs.getInt("item_id"));
        borrowing.setItemTitle(rs.getString("item_title"));
        borrowing.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
        Date returnDate = rs.getDate("return_date");
        if (returnDate != null) {
            borrowing.setReturnDate(returnDate.toLocalDate());
        }
        borrowing.setStatus(rs.getString("status"));
        return borrowing;
    }
} 