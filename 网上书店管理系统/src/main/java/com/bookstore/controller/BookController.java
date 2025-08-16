package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {
    @Autowired
    private BookRepository bookRepository;

    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @PostMapping
    public Book createBook(@RequestBody Book book) {
        // 设置默认值
        if (book.getCurrentPrice() == null) {
            book.setCurrentPrice(book.getOriginalPrice());
        }
        if (book.getStock() == null) {
            book.setStock(0);
        }
        if (book.getSalesCount() == null) {
            book.setSalesCount(0);
        }
        if (book.getViewCount() == null) {
            book.setViewCount(0);
        }
        if (book.getIsActive() == null) {
            book.setIsActive(true);
        }
        if (book.getIsFeatured() == null) {
            book.setIsFeatured(false);
        }
        return bookRepository.save(book);
    }

    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book != null) {
            book.setTitle(bookDetails.getTitle());
            book.setAuthor(bookDetails.getAuthor());
            book.setIsbn(bookDetails.getIsbn());
            book.setOriginalPrice(bookDetails.getOriginalPrice());
            book.setCurrentPrice(bookDetails.getCurrentPrice());
            book.setStock(bookDetails.getStock());
            book.setDescription(bookDetails.getDescription());
            book.setCategory(bookDetails.getCategory());
            book.setSupplier(bookDetails.getSupplier());
            return bookRepository.save(book);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookRepository.deleteById(id);
    }
}