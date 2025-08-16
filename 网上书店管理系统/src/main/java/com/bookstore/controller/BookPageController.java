package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Controller
@RequestMapping("/books")
public class BookPageController {

    @Autowired
    private BookRepository bookRepository;

    // 展示图书列表 + 表单
    @GetMapping
    public String showBooksPage(@RequestParam(value = "editId", required = false) Long editId, Model model) {
        List<Book> books = bookRepository.findAll();
        model.addAttribute("bookList", books);

        Book bookForm = (editId != null) ? bookRepository.findById(editId).orElse(new Book()) : new Book();
        model.addAttribute("bookForm", bookForm);

        return "book";
    }

    // 保存或更新图书
    @PostMapping("/save")
    public String saveOrUpdateBook(Book book) {
        // 默认值设置逻辑可移植自 RestController
        if (book.getCurrentPrice() == null) book.setCurrentPrice(book.getOriginalPrice());
        if (book.getStock() == null) book.setStock(0);
        if (book.getSalesCount() == null) book.setSalesCount(0);
        if (book.getViewCount() == null) book.setViewCount(0);
        if (book.getIsActive() == null) book.setIsActive(true);
        if (book.getIsFeatured() == null) book.setIsFeatured(false);

        bookRepository.save(book);
        return "redirect:/books";
    }

    // 删除图书
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookRepository.deleteById(id);
        return "redirect:/books";
    }
}
