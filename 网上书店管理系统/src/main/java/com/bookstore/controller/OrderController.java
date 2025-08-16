package com.bookstore.controller;


import com.bookstore.model.*;
import com.bookstore.model.DTO.OrderDTO;
import com.bookstore.model.DTO.OrderItemDTO;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.OrderRepository;
import com.bookstore.repository.ShoppingCartRepository;
import com.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {
    @Autowired
    ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Order> getOrderById(@PathVariable Long id) {
        return orderRepository.findById(id);
    }

    /**
     * 创建订单，接收前端DTO数据，转换为完整Order实体，保存。
     */
    @PostMapping
    public Order createOrder(@RequestBody OrderDTO orderDTO) {
        Order order = new Order();
        order.setOrderDate(new Date());
        order.setStatus(OrderStatus.valueOf(orderDTO.getStatus()));
        order.setShippingAddress(orderDTO.getShippingAddress());
        order.setPaymentMethod(orderDTO.getPaymentMethod());

        // 查找用户
        order.setUser(userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found")));

        BigDecimal totalAmount = BigDecimal.ZERO;
        Set<OrderItem> orderItems = new HashSet<>();

        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            Long bookId = itemDTO.getBookId(); // 🔍 重点在这里
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new RuntimeException("Book not found"));

            OrderItem item = new OrderItem();
            item.setBook(book);
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(book.getCurrentPrice());
            item.setTotalPrice(book.getCurrentPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            item.setOrder(order);

            totalAmount = totalAmount.add(item.getTotalPrice());

            // 更新书库存
            book.setSalesCount(book.getSalesCount() + item.getQuantity());
            book.setStock(book.getStock() - item.getQuantity());
            bookRepository.save(book);

            orderItems.add(item);
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        return orderRepository.save(order);
    }

    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable Long id, @RequestBody OrderDTO orderDto) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));

        if (orderDto.getStatus() != null) {
            order.setStatus(OrderStatus.valueOf(orderDto.getStatus()));
        }
        if (orderDto.getShippingAddress() != null) {
            order.setShippingAddress(orderDto.getShippingAddress());
        }
        if (orderDto.getPaymentMethod() != null) {
            order.setPaymentMethod(orderDto.getPaymentMethod());
        }

        // 订单明细更新略，需要复杂逻辑时建议单独实现

        return orderRepository.save(order);
    }

    @PostMapping("/createFromCart")
    public ResponseEntity<Order> createOrderFromCart(@RequestParam Long userId) {
        // 查询用户
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 查询购物车
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("购物车为空"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("购物车没有商品");
        }

        // 构造订单
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(new Date());
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress("默认地址");
        order.setPaymentMethod("在线支付");

        BigDecimal totalAmount = BigDecimal.ZERO;
        Set<OrderItem> orderItems = new HashSet<>();

        for (ShoppingCartItem cartItem : cart.getItems()) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setBook(cartItem.getBook());
            item.setQuantity(cartItem.getQuantity());
            item.setUnitPrice(cartItem.getUnitPrice());
            item.setTotalPrice(cartItem.getTotalPrice());
            item.setFinalPrice(cartItem.getFinalPrice()); // 🔧 关键点：不能为 null

            // 更新图书库存
            Book book = cartItem.getBook();
            book.setSalesCount(book.getSalesCount() + cartItem.getQuantity());
            book.setStock(book.getStock() - cartItem.getQuantity());
            bookRepository.save(book);

            totalAmount = totalAmount.add(item.getFinalPrice());
            orderItems.add(item);
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        // 保存订单
        Order savedOrder = orderRepository.save(order);

        // 清空购物车
        cart.getItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);
        cart.setFinalAmount(BigDecimal.ZERO);
        cart.setDiscountAmount(BigDecimal.ZERO);
        cart.setUpdatedDate(new Date());
        shoppingCartRepository.save(cart);

        return ResponseEntity.ok(savedOrder);
    }

    @PostMapping("/orders/save")
    @Transactional
    public String saveOrderFromForm(@RequestParam Long userId,
                                    @RequestParam String shippingAddress,
                                    @RequestParam String paymentMethod,
                                    @RequestParam String status,
                                    HttpServletRequest request) {

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(new Date());
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setStatus(OrderStatus.valueOf(status));

        Set<OrderItem> items = new HashSet<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Book book : bookRepository.findAll()) {
            String bookIdStr = "book_" + book.getId();
            String qtyStr = "quantity_" + book.getId();

            if (request.getParameter(bookIdStr) != null) {
                int quantity = Integer.parseInt(request.getParameter(qtyStr));
                if (quantity > 0 && book.getStock() >= quantity) {
                    BigDecimal itemTotal = book.getCurrentPrice().multiply(BigDecimal.valueOf(quantity));

                    OrderItem item = new OrderItem();
                    item.setBook(book);
                    item.setQuantity(quantity);
                    item.setUnitPrice(book.getCurrentPrice());
                    item.setTotalPrice(itemTotal);
                    item.setFinalPrice(itemTotal);
                    item.setOrder(order);

                    System.out.println("DEBUG: finalPrice = " + item.getFinalPrice());

                    total = total.add(itemTotal);

                    // 更新库存
                    book.setStock(book.getStock() - quantity);
                    book.setSalesCount(book.getSalesCount() + quantity);
                    bookRepository.save(book);

                    items.add(item);
                }
            }
        }

        order.setItems(items);
        order.setTotalAmount(total);

        orderRepository.save(order);

        return "redirect:/order/manage";
    }

//    @PostMapping("/orders/delete/{id}")
//    public String deleteOrder(@PathVariable Long id) {
//        orderRepository.deleteById(id);
//        return "redirect:/orders"; // 删除后回到订单管理页
//    }
}

