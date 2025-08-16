package com.bookstore.controller;

import com.bookstore.model.*;
import com.bookstore.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/user")
public class UserPageIndexController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    private Long testUserId = 1L;

    // 首页，导航页
    @GetMapping("/user_index")
    public String index() {
        return "user_index";  // templates/index.html
    }

    // 图书 + 购物车 页面
    @GetMapping("/books_cart")
    public String booksCart(Model model) {
        Long userId = testUserId;

        List<Book> bookList = bookRepository.findAll();
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId).orElse(new ShoppingCart());

        model.addAttribute("userId", userId);
        model.addAttribute("bookList", bookList);
        model.addAttribute("cart", cart);

        return "books_cart";  // templates/books_cart.html
    }

    // 我的订单 页面
    @GetMapping("/user_order")
    public String orders(Model model) {
        Long userId = testUserId;
        List<Order> orderList = orderRepository.findByUserId(userId);

        model.addAttribute("orderList", orderList);
        return "user_order";  // templates/orders.html
    }

    @PostMapping("/cart/add")
    @ResponseBody
    public Map<String, Object> addToCart(@RequestParam Long bookId,
                                         @RequestParam Integer quantity) {
        Map<String, Object> result = new HashMap<>();
        User user = userRepository.findById(testUserId).orElse(null);
        if (user == null) {
            result.put("success", false);
            result.put("message", "用户未找到");
            return result;
        }

        ShoppingCart cart = shoppingCartRepository.findByUserId(testUserId).orElse(null);

        if (cart == null) {
            // 新建购物车
            cart = new ShoppingCart();
            cart.setUser(user);
            cart.setCreatedDate(new Date());
            cart.setItems(new HashSet<>());
            shoppingCartRepository.save(cart);  // 先保存，生成ID
        }

        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            result.put("success", false);
            result.put("message", "图书未找到");
            return result;
        }

        Optional<ShoppingCartItem> existingOpt = cart.getItems().stream()
                .filter(i -> i.getBook().getId().equals(bookId))
                .findFirst();

        if (existingOpt.isPresent()) {
            ShoppingCartItem existingItem = existingOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            BigDecimal totalPrice = book.getCurrentPrice().multiply(BigDecimal.valueOf(existingItem.getQuantity()));
            existingItem.setTotalPrice(totalPrice);
            existingItem.setFinalPrice(totalPrice);
        } else {
            ShoppingCartItem newItem = new ShoppingCartItem();
            newItem.setShoppingCart(cart);
            newItem.setBook(book);
            newItem.setQuantity(quantity);
            newItem.setUnitPrice(book.getCurrentPrice());
            BigDecimal totalPrice = book.getCurrentPrice().multiply(BigDecimal.valueOf(quantity));
            newItem.setTotalPrice(totalPrice);
            newItem.setFinalPrice(totalPrice);
            cart.getItems().add(newItem);
        }


        updateCartTotals(cart);
        shoppingCartRepository.save(cart);

        result.put("success", true);
        result.put("message", "添加成功");
        return result;
    }


    // 更新购物车
    @PostMapping("/cart/update")
    public String updateCart(@RequestParam Long bookId,
                             @RequestParam Integer quantity) {
        ShoppingCart cart = shoppingCartRepository.findByUserId(testUserId).orElse(null);
        if (cart == null) return "redirect:/user/books_cart";

        Optional<ShoppingCartItem> itemOpt = cart.getItems().stream()
                .filter(i -> i.getBook().getId().equals(bookId))
                .findFirst();

        if (itemOpt.isPresent()) {
            ShoppingCartItem item = itemOpt.get();
            if (quantity <= 0) {
                cart.getItems().remove(item);
            } else {
                item.setQuantity(quantity);
                item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));
            }
            updateCartTotals(cart);
            shoppingCartRepository.save(cart);
        }

        return "redirect:/user/books_cart";
    }

    // 删除购物车项
    @PostMapping("/cart/remove")
    public String removeCartItem(@RequestParam Long bookId) {
        ShoppingCart cart = shoppingCartRepository.findByUserId(testUserId).orElse(null);
        if (cart == null) return "redirect:/user/books_cart";

        cart.getItems().removeIf(i -> i.getBook().getId().equals(bookId));
        updateCartTotals(cart);
        shoppingCartRepository.save(cart);

        return "redirect:/user/books_cart";
    }

    @PostMapping("/order/checkout")
    public String checkoutOrder(
            @RequestParam String shippingAddress,
            @RequestParam String paymentMethod,
            @RequestParam String status,
            @RequestParam Map<String,String> allParams
    ) {
        User user = userRepository.findById(testUserId).orElse(null);
        if (user == null) return "redirect:/user/user_index?error=userNotFound";

        Set<OrderItem> orderItems = new HashSet<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (String key : allParams.keySet()) {
            if (key.startsWith("book_")) {
                String bookIdStr = allParams.get(key);
                if (bookIdStr == null) continue;
                Long bookId = Long.parseLong(bookIdStr);

                String qtyStr = allParams.get("quantity_" + bookId);
                int qty = 1;
                try {
                    qty = Integer.parseInt(qtyStr);
                } catch (Exception e) {
                    // 默认1
                }
                if (qty <= 0) continue;

                Book book = bookRepository.findById(bookId).orElse(null);
                if (book == null) continue;

                OrderItem orderItem = new OrderItem();
                orderItem.setBook(book);
                orderItem.setQuantity(qty);
                orderItem.setUnitPrice(book.getCurrentPrice());
                BigDecimal totalPrice = book.getCurrentPrice().multiply(BigDecimal.valueOf(qty));
                orderItem.setTotalPrice(totalPrice);
                orderItem.setDiscountAmount(BigDecimal.ZERO);
                orderItem.setFinalPrice(totalPrice);

                orderItems.add(orderItem);
                totalAmount = totalAmount.add(totalPrice);
            }
        }

        if (orderItems.isEmpty()) {
            // 没选书，跳转提示
            return "redirect:/user/books_cart?error=noBooksSelected";
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(new Date());
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setStatus(OrderStatus.valueOf(status));
        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }

        orderRepository.save(order);

        // 结账后清空购物车
        ShoppingCart cart = shoppingCartRepository.findByUserId(testUserId).orElse(null);
        if (cart != null) {
            cart.getItems().clear();
            updateCartTotals(cart);
            shoppingCartRepository.save(cart);
        }

        return "redirect:/user/user_order?success=orderPlaced";
    }


    private void updateCartTotals(ShoppingCart cart) {
        BigDecimal totalAmount = cart.getItems().stream()
                .map(ShoppingCartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalAmount(totalAmount);
        cart.setFinalAmount(totalAmount);
    }
}
