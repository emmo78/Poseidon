package com.poseidoninc.poseidon.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Map;

@Controller
@Slf4j
public class HomeController {

    @RequestMapping({"/", "/home"})
    public String home(Principal user) {
//        if(user instanceof UsernamePasswordAuthenticationToken)
//        if (user instanceof OAuth2AuthenticationToken) {
        return "home";
    }

    @RequestMapping("/admin/home")
    public String adminHome() {
        return "redirect:/bidList/list";
    }
}
