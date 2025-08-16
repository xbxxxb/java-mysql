package com.bookstore.controller;

import com.bookstore.repository.UserRepository;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.OrderRepository;
import com.bookstore.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/database")
@CrossOrigin(origins = "*")
public class DatabaseController {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @GetMapping("/status")
    public Map<String, Object> getDatabaseStatus() {
        Map<String, Object> status = new HashMap<>();

        try {
            Connection connection = dataSource.getConnection();
            boolean isConnected = connection.isValid(5);
            connection.close();

            status.put("databaseConnection", isConnected ? "connected" : "disconnected");
            status.put("status", "success");
        } catch (SQLException e) {
            status.put("databaseConnection", "error");
            status.put("error", e.getMessage());
            status.put("status", "error");
        }

        try {
            long userCount = userRepository.count();
            status.put("userCount", userCount);
            status.put("userRepository", "working");
        } catch (Exception e) {
            status.put("userRepository", "error");
        }

        try {
            long bookCount = bookRepository.count();
            status.put("bookCount", bookCount);
            status.put("bookRepository", "working");
        } catch (Exception e) {
            status.put("bookRepository", "error");
        }

        return status;
    }
}