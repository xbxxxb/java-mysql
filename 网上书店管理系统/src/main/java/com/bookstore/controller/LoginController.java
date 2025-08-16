package com.bookstore.controller;

import com.bookstore.model.User;
import com.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // 对应 login.html 页面
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String role,
                          HttpSession session,
                          Model model) {

        User user = userRepository.findByUsernameAndPassword(username, password);
        if (user == null) {
            model.addAttribute("error", "用户名或密码错误");
            return "login";
        }

        // 登录成功，保存用户到 Session
        session.setAttribute("loggedInUser", user);

        if ("ADMIN".equalsIgnoreCase(role)) {
            return "redirect:/index.html";
        } else {
            return "redirect:/user/user_index";
        }
    }

}
