package com.yumefusaka.bigwork.dao;

import com.yumefusaka.bigwork.model.Book;
import com.yumefusaka.bigwork.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private static final String INSERT_BOOK = "INSERT INTO Book (title, publisher_id, category_id, total_quantity, available_quantity, price, unit) VALUES (?, ?, ?, ?, ?, ?, '本')";
    private static final String UPDATE_BOOK = "UPDATE Book SET title=?, publisher_id=?, category_id=?, total_quantity=?, available_quantity=?, price=? WHERE book_id=?";
    private static final String DELETE_BOOK = "DELETE FROM Book WHERE book_id=?";
    private static final String SELECT_ALL_BOOKS = "SELECT b.*, p.name as publisher_name, bc.name as category_name FROM Book b LEFT JOIN Publisher p ON b.publisher_id = p.publisher_id LEFT JOIN BookCategory bc ON b.category_id = bc.category_id";
    private static final String SELECT_BOOK_BY_ID = "SELECT b.*, p.name as publisher_name, bc.name as category_name FROM Book b LEFT JOIN Publisher p ON b.publisher_id = p.publisher_id LEFT JOIN BookCategory bc ON b.category_id = bc.category_id WHERE b.book_id=?";
    private static final String SEARCH_BOOKS = "SELECT b.*, p.name as publisher_name, bc.name as category_name FROM Book b LEFT JOIN Publisher p ON b.publisher_id = p.publisher_id LEFT JOIN BookCategory bc ON b.category_id = bc.category_id WHERE b.title LIKE ? OR p.name LIKE ?";

    public void insert(Book book) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_BOOK, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, book.getTitle());
            pstmt.setInt(2, book.getPublisherId());
            pstmt.setInt(3, book.getCategoryId());
            pstmt.setInt(4, book.getTotalQuantity());
            pstmt.setInt(5, book.getAvailableQuantity());
            pstmt.setDouble(6, book.getPrice());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    book.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void update(Book book) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_BOOK)) {
            
            pstmt.setString(1, book.getTitle());
            pstmt.setInt(2, book.getPublisherId());
            pstmt.setInt(3, book.getCategoryId());
            pstmt.setInt(4, book.getTotalQuantity());
            pstmt.setInt(5, book.getAvailableQuantity());
            pstmt.setDouble(6, book.getPrice());
            pstmt.setInt(7, book.getId());
            
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_BOOK)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public List<Book> findAll() throws SQLException {
        List<Book> books = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_BOOKS)) {
            
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        }
        return books;
    }

    public Book findById(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BOOK_BY_ID)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBook(rs);
                }
            }
        }
        return null;
    }

    public List<Book> search(String keyword) throws SQLException {
        List<Book> books = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SEARCH_BOOKS)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
        }
        return books;
    }

    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("book_id"));
        book.setTitle(rs.getString("title"));
        book.setPublisherId(rs.getInt("publisher_id"));
        book.setCategoryId(rs.getInt("category_id"));
        book.setTotalQuantity(rs.getInt("total_quantity"));
        book.setAvailableQuantity(rs.getInt("available_quantity"));
        book.setPrice(rs.getDouble("price"));
        book.setPublisherName(rs.getString("publisher_name"));
        book.setCategoryName(rs.getString("category_name"));
        return book;
    }
} 