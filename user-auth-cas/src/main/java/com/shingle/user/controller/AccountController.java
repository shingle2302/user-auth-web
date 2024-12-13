package com.shingle.user.controller;


import com.shingle.user.entity.User;
import com.shingle.user.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/")
@Controller
public class AccountController {

    @Resource
    private UserService userService;

    @GetMapping(path = {"/", "/home"})
    public String home(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        User user = userService.authenticate(username, password);
        if (user != null) {
            session.setAttribute("user", user);
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping("/ssoLogin")
    public String casLogin(HttpSession session, Model model) {
        // 这里可以添加CAS登录后的处理逻辑
        // 例如，从CAS服务器获取用户信息并设置到session中
        // 这里假设CAS已经处理了登录，并将用户信息存储在session中
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:cas/login";
        }
        model.addAttribute("user", user);
        return "redirect:/home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}


