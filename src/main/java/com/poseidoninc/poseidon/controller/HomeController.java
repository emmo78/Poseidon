package com.poseidoninc.poseidon.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@Slf4j
public class HomeController {

    @RequestMapping({"/", "/home"})
    public String home(Principal user) {
//        if(user instanceof UsernamePasswordAuthenticationToken)
//        if (user instanceof OAuth2AuthenticationToken) {     }
        return "home";
    }

    @RequestMapping("/admin/home")
    public String adminHome() {
        return "redirect:/bidList/list";
    }
}
