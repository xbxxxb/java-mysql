package com.bookstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/suppliers")
public class SupplierPageController {

    @GetMapping
    public String supplierPage() {
        return "supplier"; // 对应 resources/templates/supplier.html
    }
}
