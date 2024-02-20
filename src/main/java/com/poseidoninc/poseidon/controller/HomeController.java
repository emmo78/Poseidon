package com.poseidoninc.poseidon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
public class HomeController {

    @RequestMapping({"/", "/home"})
    public String home() {
        return "home";
    }

    @RequestMapping("/admin/home")
    public String adminHome() {
        return "redirect:/bidList/list";
    }
}
