package com.bookstore.repository;

import com.bookstore.model.Order;
import com.bookstore.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    List<Order> findByStatus(OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    List<Order> findByOrderDateBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.orderDate BETWEEN :startDate AND :endDate")
    List<Order> findByUserIdAndOrderDateBetween(@Param("userId") Long userId, @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);
}