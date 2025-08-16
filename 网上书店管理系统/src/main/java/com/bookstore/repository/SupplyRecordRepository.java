package com.bookstore.repository;

import com.bookstore.model.SupplyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SupplyRecordRepository extends JpaRepository<SupplyRecord, Long> {
    List<SupplyRecord> findBySupplierId(Long supplierId);

    List<SupplyRecord> findByBookId(Long bookId);

    List<SupplyRecord> findByStatus(SupplyRecord.SupplyStatus status);

    @Query("SELECT sr FROM SupplyRecord sr WHERE sr.supplyDate BETWEEN :startDate AND :endDate")
    List<SupplyRecord> findBySupplyDateBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}