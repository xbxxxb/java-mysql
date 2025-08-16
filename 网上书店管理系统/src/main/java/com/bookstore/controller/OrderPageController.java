package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.model.Order;
import com.bookstore.model.OrderItem;
import com.bookstore.model.OrderStatus;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.OrderRepository;
import com.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/orders")
public class OrderPageController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private OrderRepository orderRepository;

    // 展示订单页面（含下单表单 + 已有订单）
    @GetMapping
    public String showOrderPage(Model model) {
        model.addAttribute("userList", userRepository.findAll());
        model.addAttribute("bookList", bookRepository.findAll());
        model.addAttribute("orderList", orderRepository.findAll());
        return "order";
    }
    @PostMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderRepository.deleteById(id);
        return "redirect:/orders"; // 重定向回订单管理页
    }
// 提交订单表单（注意这是假设简化的表单，不使用 DTO）
    @PostMapping("/save")
    public String saveOrder(HttpServletRequest request) {
        Long userId = Long.parseLong(request.getParameter("userId"));
        String shippingAddress = request.getParameter("shippingAddress");
        String paymentMethod = request.getParameter("paymentMethod");
        String status = request.getParameter("status");

        Order order = new Order();
        order.setOrderDate(new Date());
        order.setUser(userRepository.findById(userId).orElseThrow());
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setStatus(OrderStatus.valueOf(status));

        Set<OrderItem> items = new HashSet<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Book book : bookRepository.findAll()) {
            String bookIdStr = "book_" + book.getId();
            String qtyStr = "quantity_" + book.getId();
            if (request.getParameter(bookIdStr) != null) {
                int qty = Integer.parseInt(request.getParameter(qtyStr));

                BigDecimal unitPrice = book.getCurrentPrice();
                BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(qty));
                BigDecimal discountAmount = BigDecimal.ZERO; // 如果没有优惠，赋0
                BigDecimal finalPrice = totalPrice.subtract(discountAmount);

                OrderItem item = new OrderItem();
                item.setBook(book);
                item.setQuantity(qty);
                item.setUnitPrice(unitPrice);
                item.setTotalPrice(totalPrice);
                item.setDiscountAmount(discountAmount);
                item.setFinalPrice(finalPrice);
                item.setOrder(order);

                // 更新库存和销量
                book.setStock(book.getStock() - qty);
                book.setSalesCount(book.getSalesCount() + qty);
                bookRepository.save(book);

                totalAmount = totalAmount.add(finalPrice);
                items.add(item);
            }
        }


        order.setItems(items);
        order.setTotalAmount(totalAmount);

        orderRepository.save(order);
        return "redirect:/orders";
    }
}
