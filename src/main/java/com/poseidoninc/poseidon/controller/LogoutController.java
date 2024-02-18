package com.poseidoninc.poseidon.controller;

import com.poseidoninc.poseidon.service.RequestService;
import jakarta.servlet.ServletException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.security.Principal;

@Controller
@AllArgsConstructor
@Slf4j
public class LogoutController {

    private final RequestService requestService;

    @RequestMapping ("/app-logout")
    public String logoff(Principal user, WebRequest request) throws ServletException {
        log.info("{} : {} : registered={} log off",
                requestService.requestToString(request),
                ((ServletWebRequest) request).getHttpMethod(),
                user.getName());
        ((ServletWebRequest) request).getRequest().logout();
        return "redirect:/";
    }

}
