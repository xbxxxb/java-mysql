package com.bookstore.repository;

import com.bookstore.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findByIsActiveTrue();

    List<Supplier> findByCreditRatingGreaterThanEqual(Integer minCreditRating);

}