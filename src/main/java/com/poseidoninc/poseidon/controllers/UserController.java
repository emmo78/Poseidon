package com.poseidoninc.poseidon.controllers;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.User;
import com.poseidoninc.poseidon.repositories.UserRepository;
import com.poseidoninc.poseidon.services.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/user/list")
	public String home(Model model, WebRequest request) {
		Pageable pageRequest = Pageable.unpaged();
		model.addAttribute("users", userService.getUsers(pageRequest, request));
		return "user/list";
	}

	@GetMapping("/user/add")
	public String addUser(User user) {
		return "user/add";
	}

	@PostMapping("/user/validate")
	public String validate(@Valid User user, BindingResult result, WebRequest request) {
		if (result.hasErrors()) {
			return "user/add";
		}
		userService.saveUser(user, request);
		return "redirect:/user/list";

	}

	@GetMapping("/user/update/{id}")
	public String showUpdateForm(@PathVariable("id") Integer id, Model model, WebRequest request) {
		User user = userService.getUserById(id, request);
		user.setPassword("");
		model.addAttribute("user", user);
		return "user/update";
	}

	@PostMapping("/user/update")
	public String updateUser(@Valid User user, BindingResult result, WebRequest request) {
		if (result.hasErrors()) {
			return "user/update";
		}
		userService.saveUser(user, request);
		return "redirect:/user/list";
	}

	@GetMapping("/user/delete/{id}")
	public String deleteUser(@PathVariable("id") Integer id, Model model, WebRequest request) {
		userService.deleteUser(id, request);
		return "redirect:/user/list";
	}
}
