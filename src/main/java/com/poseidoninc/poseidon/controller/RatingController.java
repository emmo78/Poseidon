package com.poseidoninc.poseidon.controller;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.Rating;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import com.poseidoninc.poseidon.service.RatingService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class RatingController {
    
	private final RatingService ratingService;

	@GetMapping("/rating/list")
    public String home(Model model, WebRequest request) throws UnexpectedRollbackException {
		Pageable pageRequest = Pageable.unpaged();
		model.addAttribute("ratings", ratingService.getRatings(pageRequest, request));
		return "rating/list";
    }

    @GetMapping("/rating/add")
    public String addRatingForm(Rating rating) {
        return "rating/add";
    }

    @PostMapping("/rating/validate")
    public String validate(@Valid Rating rating, BindingResult result, WebRequest request) throws UnexpectedRollbackException {
		if (result.hasErrors()) {
        	return "rating/add";
		}
		ratingService.saveRating(rating, request);
		return "redirect:/rating/list";		
    }

    @GetMapping("/rating/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model, WebRequest request)  throws UnexpectedRollbackException {
        Rating rating = ratingService.getRatingById(id, request);
		model.addAttribute("rating", rating);       
        return "rating/update";
    }

    @PostMapping("/rating/update")
    public String updateRating(@Valid Rating rating, BindingResult result, WebRequest request) throws UnexpectedRollbackException {
		if (result.hasErrors()) {
			return "rating/update";			
		}
		ratingService.saveRating(rating, request);
		return "redirect:/rating/list";
    }

    @GetMapping("/rating/delete/{id}")
    public String deleteRating(@PathVariable("id") Integer id, WebRequest request) throws UnexpectedRollbackException {
        ratingService.deleteRatingById(id, request);
        return "redirect:/rating/list";
    }
}
