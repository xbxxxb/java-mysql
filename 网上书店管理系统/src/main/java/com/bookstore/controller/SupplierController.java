package com.bookstore.controller;

import com.bookstore.model.Supplier;
import com.bookstore.model.SupplyRecord;
import com.bookstore.repository.SupplierRepository;
import com.bookstore.repository.SupplyRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin(origins = "*")
public class SupplierController {
    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplyRecordRepository supplyRecordRepository;

    @GetMapping
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findByIsActiveTrue();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable Long id) {
        Optional<Supplier> supplier = supplierRepository.findById(id);
        return supplier.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Supplier createSupplier(@RequestBody Supplier supplier) {
        supplier.setRegistrationDate(new Date());
        supplier.setIsActive(true);
        // 其他默认值可以根据需要设置
        return supplierRepository.save(supplier);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplierDetails) {
        Optional<Supplier> supplierOpt = supplierRepository.findById(id);
        if (!supplierOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Supplier supplier = supplierOpt.get();

        // 合并更新，防止覆盖丢失
        if (supplierDetails.getName() != null) supplier.setName(supplierDetails.getName());
        if (supplierDetails.getContactPerson() != null) supplier.setContactPerson(supplierDetails.getContactPerson());
        if (supplierDetails.getPhoneNumber() != null) supplier.setPhoneNumber(supplierDetails.getPhoneNumber());
        if (supplierDetails.getEmail() != null) supplier.setEmail(supplierDetails.getEmail());
        if (supplierDetails.getCompanyAddress() != null) supplier.setCompanyAddress(supplierDetails.getCompanyAddress());
        if (supplierDetails.getCreditRating() != null) supplier.setCreditRating(supplierDetails.getCreditRating());

        supplierRepository.save(supplier);

        return ResponseEntity.ok(supplier);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        Optional<Supplier> supplierOpt = supplierRepository.findById(id);
        if (!supplierOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Supplier supplier = supplierOpt.get();
        supplier.setIsActive(false);
        supplierRepository.save(supplier);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/supply-records")
    public List<SupplyRecord> getSupplierSupplyRecords(@PathVariable Long id) {
        return supplyRecordRepository.findBySupplierId(id);
    }

    @GetMapping("/high-credit")
    public List<Supplier> getHighCreditSuppliers(@RequestParam(defaultValue = "8") Integer minCreditRating) {
        return supplierRepository.findByCreditRatingGreaterThanEqual(minCreditRating);
    }
}