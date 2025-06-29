package com.project.spring.pawple.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.spring.pawple.app.user.UserEntity;
import com.project.spring.pawple.app.user.UserRepository;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@Controller
public class PageController {

    @Autowired
    private UserRepository userRep;


    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @GetMapping("/signin")
    public String signin() {
        return "signin";
    }

    @GetMapping("/pet")
    public String pet() {
        return "pet";
    }

    @GetMapping("/findid")
    public String findid() {
        return "findid";
    }

    @GetMapping("/findpw")
    public String findPasswordPage() {
        return "findpw";
    }

    @GetMapping("/resetpw")
    public String resetPasswordPage(@RequestParam("userId") String userId, Model model) {
        model.addAttribute("userId", userId);
        return "resetpw"; 
    }
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            UserEntity user = userRep.findByName(userDetails.getUsername()).orElse(null);
            if (user != null) {
                model.addAttribute("user", user);
            }
        }
        return "profile";
    }

    
}
