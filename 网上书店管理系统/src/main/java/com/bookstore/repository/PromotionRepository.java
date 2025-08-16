package com.bookstore.repository;

import com.bookstore.model.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findByIsActiveTrue();

    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND p.startDate <= :currentDate AND p.endDate >= :currentDate")
    List<Promotion> findActivePromotions(@Param("currentDate") Date currentDate);

    List<Promotion> findByBookIdAndIsActiveTrue(Long bookId);

    List<Promotion> findByCategoryIdAndIsActiveTrue(Long categoryId);

    List<Promotion> findByUserRoleIdAndIsActiveTrue(Long userRoleId);
}