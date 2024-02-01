package com.poseidoninc.poseidon.controller;

import com.poseidoninc.poseidon.domain.User;
import com.poseidoninc.poseidon.service.RequestService;
import com.poseidoninc.poseidon.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
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
@Slf4j
public class UserController {

    private final UserService userService;
    private final RequestService requestService;

    @GetMapping("/user/list")
    public String home(Model model, WebRequest request) throws UnexpectedRollbackException {
        Pageable pageRequest = Pageable.unpaged();
        Page<User> pageUser;
        try {
            pageUser = userService.getUsers(pageRequest);
        } catch(UnexpectedRollbackException urbe) {
            log.error("{} : {} ", requestService.requestToString(request), urbe.toString());
            throw new UnexpectedRollbackException("Error while getting users");
        }
        log.info("{} : users page number : {} of {}",
                requestService.requestToString(request),
                pageUser.getNumber()+1,
                pageUser.getTotalPages());
        model.addAttribute("users", pageUser);
        return "user/list";
    }

    @GetMapping("/user/add")
    public String addUser(User user) {
        return "user/add";
    }

    @PostMapping("/user/validate")
    public String validate(@Valid User user, BindingResult result, WebRequest request) throws UnexpectedRollbackException {
        if (result.hasErrors()) {
            log.error("{} : user = {} : {} ", requestService.requestToString(request), user.toString(), result.toString());
            return "user/add";
        }
        User userSaved;
        try {
            userSaved = userService.saveUser(user);
        } catch (DataIntegrityViolationException dive) {
            log.error("{} : user = {} : {} ", requestService.requestToString(request), user.toString(), dive.toString());
            result.addError(new FieldError("User", "curveId", dive.getMessage()));
            return "user/add";
        } catch (UnexpectedRollbackException urbe) {
            log.error("{} : user = {} : {} ", requestService.requestToString(request), user.toString(), urbe.toString());
            throw new UnexpectedRollbackException("Error while saving user");
        }
        log.info("{} : user = {} persisted", requestService.requestToString(request), userSaved.toString());
        return "redirect:/user/list";
    }

    @GetMapping("/user/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model, WebRequest request) throws UnexpectedRollbackException {
        User user;
        try {
            user = userService.getUserByIdWithBlankPasswd(id);
        }  catch (UnexpectedRollbackException urbe) {
            log.error("{} : user = {} : {} ", requestService.requestToString(request), id, urbe.toString());
            throw new UnexpectedRollbackException("Error while getting user");
        }
        log.info("{} : user = {} gotten",  requestService.requestToString(request), user.toString());
        model.addAttribute("user", user);
        return "user/update";
    }

    @PostMapping("/user/update")
    public String updateUser(@Valid User user, BindingResult result, WebRequest request) throws UnexpectedRollbackException {
        if (result.hasErrors()) {
            log.error("{} : user = {} : {} ", requestService.requestToString(request), user.toString(), result.toString());
            return "user/update";
        }
        User userUpdated;
        try {
            userUpdated = userService.saveUser(user);
        } catch (DataIntegrityViolationException dive) {
            log.error("{} : user = {} : {} ", requestService.requestToString(request), user.toString(), dive.toString());
            result.addError(new FieldError("User", "curveId", dive.getMessage()));
            return "user/update";
        } catch (UnexpectedRollbackException urbe) {
            log.error("{} : user = {} : {} ", requestService.requestToString(request), user.toString(), urbe.toString());
            throw new UnexpectedRollbackException("Error while saving user");
        }
        log.info("{} : user = {} persisted", requestService.requestToString(request), userUpdated.toString());
        return "redirect:/user/list";
    }

    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id, WebRequest request) throws UnexpectedRollbackException {
        try {
            userService.deleteUserById(id);
        } catch (UnexpectedRollbackException urbe) {
            log.error("{} : user = {} : {} ", requestService.requestToString(request), id, urbe.toString());
            throw new UnexpectedRollbackException("Error while deleting user");
        }
        log.info("{} : user = {} deleted", requestService.requestToString(request), id);
        return "redirect:/user/list";
    }
}
