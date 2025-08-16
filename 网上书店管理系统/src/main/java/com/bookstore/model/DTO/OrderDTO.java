package com.bookstore.model.DTO;
import java.util.List;

public class OrderDTO {
    private Long userId;
    private List<OrderItemDTO> items;
    private String status;
    private String shippingAddress;
    private String paymentMethod;

    // getters 和 setters
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public List<OrderItemDTO> getItems() {
        return items;
    }
    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getShippingAddress() {
        return shippingAddress;
    }
    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
    public String getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
