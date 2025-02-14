package com.poseidoninc.poseidon.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * LoginController class handles HTTP requests related to log in management
 *
 * @author olivier morel
 *
 */
@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage(Authentication authentication) {
        if (authentication!=null && authentication.isAuthenticated()) {
            return "redirect:/home";
        }
        return "login";
    }
}
