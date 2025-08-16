package com.bookstore.controller;


import com.bookstore.model.Book;
import com.bookstore.model.User;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
@Controller
@RequestMapping("/users")
public class UserPageController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 现有的用户列表、保存、删除、注册页面方法 ...

    @GetMapping
    public String showUserPage(@RequestParam(value = "editId", required = false) Long editId, Model model) {
        model.addAttribute("userList", userRepository.findAll());
        User formUser = (editId != null) ? userRepository.findById(editId).orElse(new User()) : new User();
        model.addAttribute("userForm", formUser);
        return "user";
    }
    @PostMapping("/saves")
    public String saveUsers(@ModelAttribute User user) {
        if (user.getId() == null) {
            // 取消加密，直接存明文密码
            // user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setPassword(user.getPassword());

            user.setRegistrationDate(new Date());
            user.setIsActive(true);
            user.setIsVerified(false);
        } else {
            User existing = userRepository.findById(user.getId()).orElseThrow();
            existing.setUsername(user.getUsername());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                // 取消加密
                // existing.setPassword(passwordEncoder.encode(user.getPassword()));
                existing.setPassword(user.getPassword());
            }
            existing.setFullName(user.getFullName());
            existing.setPhoneNumber(user.getPhoneNumber());
            existing.setEmail(user.getEmail());
            user = existing;
        }
        userRepository.save(user);
        return "redirect:/users";
    }
    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user) {
        if (user.getId() == null) {
            // 取消加密，直接存明文密码
            // user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setPassword(user.getPassword());

            user.setRegistrationDate(new Date());
            user.setIsActive(true);
            user.setIsVerified(false);
        } else {
            User existing = userRepository.findById(user.getId()).orElseThrow();
            existing.setUsername(user.getUsername());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                // 取消加密
                // existing.setPassword(passwordEncoder.encode(user.getPassword()));
                existing.setPassword(user.getPassword());
            }
            existing.setFullName(user.getFullName());
            existing.setPhoneNumber(user.getPhoneNumber());
            existing.setEmail(user.getEmail());
            user = existing;
        }
        userRepository.save(user);
        return "redirect:/users";
    }


    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/users";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("userForm", new User());
        return "register";  // 映射到 templates/register.html
    }


    // 新增：检查用户名是否重复
    @GetMapping("/checkUsername")
    @ResponseBody
    public boolean checkUsername(@RequestParam String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    // 新增：检查手机号是否重复
    @GetMapping("/checkPhoneNumber")
    @ResponseBody
    public boolean checkPhoneNumber(@RequestParam String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

    // 新增：检查邮箱是否重复
    @GetMapping("/checkEmail")
    @ResponseBody
    public boolean checkEmail(@RequestParam String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}

