package com.bookstore.repository;

import com.bookstore.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByIsActiveTrue();

    List<Department> findByParentDepartmentIsNull();

    List<Department> findByParentDepartmentId(Long parentId);
}