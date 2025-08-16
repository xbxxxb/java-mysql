package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.model.ShoppingCart;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.ShoppingCartRepository;
import com.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Controller
@RequestMapping("/cart")
public class ShoppingCartPageController {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;

    private Long testUserId = 1L; // ⚠️ 默认测试用用户ID，可改为登录用户

    @GetMapping
    public String viewCart(Model model) {
        Long userId = testUserId;
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId).orElse(new ShoppingCart());
        model.addAttribute("userId", userId);
        model.addAttribute("cart", cart);
        model.addAttribute("bookList", bookRepository.findAll());
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long userId,
                            @RequestParam Long bookId,
                            @RequestParam Integer quantity) {
        // 重用你的 REST Controller 逻辑
        // 这里简化逻辑，可复制对应服务类逻辑
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateItem(@RequestParam Long userId,
                             @RequestParam Long bookId,
                             @RequestParam Integer quantity) {
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeItem(@RequestParam Long userId,
                             @RequestParam Long bookId) {
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(@RequestParam Long userId) {
        return "redirect:/cart";
    }

}
