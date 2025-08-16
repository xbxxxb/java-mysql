package com.bookstore.model.DTO;
// OrderItemDTO.java

public class OrderItemDTO {
    private Long bookId;
    private Integer quantity;

    // getters 和 setters
    public Long getBookId() {
        return bookId;
    }
    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
