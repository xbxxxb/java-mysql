package com.bookstore.controller;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/analytics")
    public String analyticsPage() {
        return "analytics"; // 返回 templates/analytics.html 页面
    }
}
