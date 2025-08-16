package com.bookstore.controller;

import com.bookstore.model.ShoppingCart;
import com.bookstore.model.ShoppingCartItem;
import com.bookstore.model.Book;
import com.bookstore.model.User;
import com.bookstore.repository.ShoppingCartRepository;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<ShoppingCart> getCart(@PathVariable Long userId) {
        Optional<ShoppingCart> cart = shoppingCartRepository.findByUserId(userId);
        return cart.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<ShoppingCart> addToCart(
            @PathVariable Long userId,
            @RequestParam Long bookId,
            @RequestParam Integer quantity) {

        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Book> bookOpt = bookRepository.findById(bookId);

        if (!userOpt.isPresent() || !bookOpt.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        User user = userOpt.get();
        Book book = bookOpt.get();

        // 获取或创建购物车
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElse(new ShoppingCart());

        if (cart.getId() == null) {
            cart.setUser(user);
            cart.setCreatedDate(new Date());
        }

        cart.setUpdatedDate(new Date());

        // 检查购物车中是否已有该商品
        ShoppingCartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getBook().getId().equals(bookId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // 更新数量
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setTotalPrice(book.getCurrentPrice().multiply(BigDecimal.valueOf(existingItem.getQuantity())));
            existingItem.setFinalPrice(existingItem.getTotalPrice().subtract(existingItem.getDiscountAmount()));
        } else {
            // 添加新商品
            ShoppingCartItem newItem = new ShoppingCartItem();
            newItem.setShoppingCart(cart);
            newItem.setBook(book);
            newItem.setQuantity(quantity);
            newItem.setUnitPrice(book.getCurrentPrice());
            newItem.setTotalPrice(book.getCurrentPrice().multiply(BigDecimal.valueOf(quantity)));
            newItem.setDiscountAmount(BigDecimal.ZERO);
            newItem.setFinalPrice(newItem.getTotalPrice());
            cart.getItems().add(newItem);
        }

        // 重新计算总金额
        updateCartTotals(cart);

        ShoppingCart savedCart = shoppingCartRepository.save(cart);
        return ResponseEntity.ok(savedCart);
    }

    @PostMapping("/{userId}/update")
    public ResponseEntity<ShoppingCart> updateCartItem(
            @PathVariable Long userId,
            @RequestParam Long bookId,
            @RequestParam Integer quantity) {

        Optional<ShoppingCart> cartOpt = shoppingCartRepository.findByUserId(userId);
        if (!cartOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        ShoppingCart cart = cartOpt.get();

        ShoppingCartItem item = cart.getItems().stream()
                .filter(i -> i.getBook().getId().equals(bookId))
                .findFirst()
                .orElse(null);

        if (item == null) {
            return ResponseEntity.notFound().build();
        }

        if (quantity <= 0) {
            cart.getItems().remove(item);
        } else {
            item.setQuantity(quantity);
            item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));
            item.setFinalPrice(item.getTotalPrice().subtract(item.getDiscountAmount()));
        }

        cart.setUpdatedDate(new Date());
        updateCartTotals(cart);

        ShoppingCart savedCart = shoppingCartRepository.save(cart);
        return ResponseEntity.ok(savedCart);
    }

    @DeleteMapping("/{userId}/remove")
    public ResponseEntity<ShoppingCart> removeFromCart(
            @PathVariable Long userId,
            @RequestParam Long bookId) {

        Optional<ShoppingCart> cartOpt = shoppingCartRepository.findByUserId(userId);
        if (!cartOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        ShoppingCart cart = cartOpt.get();

        cart.getItems().removeIf(item -> item.getBook().getId().equals(bookId));
        cart.setUpdatedDate(new Date());
        updateCartTotals(cart);

        ShoppingCart savedCart = shoppingCartRepository.save(cart);
        return ResponseEntity.ok(savedCart);
    }

    @PostMapping("/{userId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        Optional<ShoppingCart> cartOpt = shoppingCartRepository.findByUserId(userId);
        if (cartOpt.isPresent()) {
            ShoppingCart cart = cartOpt.get();
            cart.getItems().clear();
            updateCartTotals(cart);
            shoppingCartRepository.save(cart);
        }
        return ResponseEntity.ok().build();
    }

    private void updateCartTotals(ShoppingCart cart) {
        BigDecimal totalAmount = cart.getItems().stream()
                .map(ShoppingCartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDiscount = cart.getItems().stream()
                .map(ShoppingCartItem::getDiscountAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalAmount(totalAmount);
        cart.setDiscountAmount(totalDiscount);
        cart.setFinalAmount(totalAmount.subtract(totalDiscount));
    }
}