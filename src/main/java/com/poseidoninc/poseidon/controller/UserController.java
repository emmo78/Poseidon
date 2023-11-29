package com.poseidoninc.poseidon.controller;

import com.poseidoninc.poseidon.domain.User;
import com.poseidoninc.poseidon.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;

@Controller
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user/list")
    public String home(Model model, WebRequest request) throws UnexpectedRollbackException {
        Pageable pageRequest = Pageable.unpaged();
        model.addAttribute("users", userService.getUsers(pageRequest, request));
        return "user/list";
    }

    @GetMapping("/user/add")
    public String addUser(User user) {
        return "user/add";
    }

    @PostMapping("/user/validate")
    public String validate(@Valid User user, BindingResult result, WebRequest request) throws UnexpectedRollbackException {
        if (result.hasErrors()) {
            return "user/add";
        }
        try {
            userService.saveUser(user, request);
        } catch (DataIntegrityViolationException dive) {
            result.addError(new FieldError("User", "username", dive.getMessage()));
            return "user/add";
        }
        return "redirect:/user/list";
    }

    @GetMapping("/user/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model, WebRequest request) throws UnexpectedRollbackException {
        User user = userService.getUserByIdWithBlankPasswd(id, request);
        model.addAttribute("user", user);
        return "user/update";
    }

    @PostMapping("/user/update")
    public String updateUser(@Valid User user, BindingResult result, WebRequest request) throws UnexpectedRollbackException {
        if (result.hasErrors()) {
            return "user/update";
        }
        try {
            userService.saveUser(user, request);
        } catch (DataIntegrityViolationException dive) {
            result.addError(new FieldError("User", "username", dive.getMessage()));
            return "user/update";
        }
        return "redirect:/user/list";
    }

    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id, WebRequest request) throws UnexpectedRollbackException {
        userService.deleteUserById(id, request);
        return "redirect:/user/list";
    }
}
