package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.model.Order;
import com.bookstore.model.User;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.OrderRepository;
import com.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/sales-summary")
    public Map<String, Object> getSalesSummary() {
        Map<String, Object> summary = new HashMap<>();

        // 总销售额
        List<Order> allOrders = orderRepository.findAll();
        BigDecimal totalSales = allOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 订单数量
        long totalOrders = allOrders.size();

        // 平均订单金额
        BigDecimal averageOrderValue = totalOrders > 0
                ? totalSales.divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;

        summary.put("totalSales", totalSales);
        summary.put("totalOrders", totalOrders);
        summary.put("averageOrderValue", averageOrderValue);

        return summary;
    }

    @GetMapping("/top-selling-books")
    public List<Map<String, Object>> getTopSellingBooks(@RequestParam(defaultValue = "10") int limit) {
        List<Book> books = bookRepository.findAll();

        return books.stream()
                .sorted((b1, b2) -> Integer.compare(b2.getSalesCount(), b1.getSalesCount()))
                .limit(limit)
                .map(book -> {
                    Map<String, Object> bookData = new HashMap<>();
                    bookData.put("id", book.getId());
                    bookData.put("title", book.getTitle());
                    bookData.put("author", book.getAuthor());
                    bookData.put("salesCount", book.getSalesCount());
                    bookData.put("currentPrice", book.getCurrentPrice());
                    return bookData;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/user-statistics")
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> stats = new HashMap<>();

        List<User> allUsers = userRepository.findAll();

        // 总用户数
        long totalUsers = allUsers.size();

        // 活跃用户数
        long activeUsers = allUsers.stream()
                .filter(user -> user.getIsActive())
                .count();

        // 新注册用户数（最近30天）
        Date thirtyDaysAgo = new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000);
        long newUsers = allUsers.stream()
                .filter(user -> user.getRegistrationDate() != null &&
                        user.getRegistrationDate().after(thirtyDaysAgo))
                .count();

        stats.put("totalUsers", totalUsers);
        stats.put("activeUsers", activeUsers);
        stats.put("newUsersLast30Days", newUsers);
        stats.put("activeUserPercentage", totalUsers > 0 ? (double) activeUsers / totalUsers * 100 : 0);

        return stats;
    }

    @GetMapping("/inventory-status")
    public Map<String, Object> getInventoryStatus() {
        Map<String, Object> status = new HashMap<>();

        List<Book> books = bookRepository.findAll();

        // 库存不足的图书（库存少于10本）
        long lowStockBooks = books.stream()
                .filter(book -> book.getStock() < 10)
                .count();

        // 缺货图书
        long outOfStockBooks = books.stream()
                .filter(book -> book.getStock() <= 0)
                .count();

        // 总图书数量
        long totalBooks = books.size();

        // 平均库存
        double averageStock = books.stream()
                .mapToInt(Book::getStock)
                .average()
                .orElse(0.0);

        status.put("totalBooks", totalBooks);
        status.put("lowStockBooks", lowStockBooks);
        status.put("outOfStockBooks", outOfStockBooks);
        status.put("averageStock", averageStock);

        return status;
    }

    @GetMapping("/category-performance")
    public List<Map<String, Object>> getCategoryPerformance() {
        List<Book> books = bookRepository.findAll();

        // 按分类统计
        Map<String, List<Book>> booksByCategory = books.stream()
                .filter(book -> book.getCategory() != null)
                .collect(Collectors.groupingBy(book -> book.getCategory().getName()));

        return booksByCategory.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> categoryData = new HashMap<>();
                    String categoryName = entry.getKey();
                    List<Book> categoryBooks = entry.getValue();

                    int totalSales = categoryBooks.stream()
                            .mapToInt(Book::getSalesCount)
                            .sum();

                    BigDecimal totalRevenue = categoryBooks.stream()
                            .map(book -> book.getCurrentPrice().multiply(BigDecimal.valueOf(book.getSalesCount())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    categoryData.put("categoryName", categoryName);
                    categoryData.put("bookCount", categoryBooks.size());
                    categoryData.put("totalSales", totalSales);
                    categoryData.put("totalRevenue", totalRevenue);

                    return categoryData;
                })
                .sorted((c1, c2) -> ((BigDecimal) c2.get("totalRevenue"))
                        .compareTo((BigDecimal) c1.get("totalRevenue")))
                .collect(Collectors.toList());
    }
}