package com.poseidoninc.poseidon.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

/**
 * HomeController class handles HTTP requests related to home management.
 *
 * @author olivier morel
 */
@Controller
@Slf4j
public class HomeController {

    @RequestMapping({"/", "/home"})
    public String home(Principal user) {
        return "home";
    }

    @RequestMapping("/admin/home")
    public String adminHome() {
        return "redirect:/bidList/list";
    }
}
