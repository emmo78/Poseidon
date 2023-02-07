package com.poseidoninc.poseidon.controllers;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
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
	private final UserRepository userRepository;

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
	public String validate(@Valid User user, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "user/add";
		}

		userRepository.save(user);
		return "redirect:/user/list";

	}

/*	@GetMapping("/user/update/{id}")
	public String showUpdateForm(@PathVariable("id") int id, Model model) {
		User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
		user.setPassword("");
		model.addAttribute("user", user);
		return "user/update";
	}

	@PostMapping("/user/update/{id}")
	public String updateUser(@PathVariable("id") int id, @Valid User user, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "user/update";
		}

		user.setPassword(encoder.encode(user.getPassword()));
		user.setId(id);
		userRepository.save(user);
		return "redirect:/user/list";
	}

	@GetMapping("/user/delete/{id}")
	public String deleteUser(@PathVariable("id") int id, Model model) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
		userRepository.delete(user);
		model.addAttribute("users", userRepository.findAll());
		return "redirect:/user/list";
	}
	*/
}
