package com.bookstore.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {

    @GetMapping("/connection")
    public Map<String, Object> testConnection() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "前后端连接成功！");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("service", "网上书店管理系统");
        response.put("version", "1.0.0");
        return response;
    }
}