package com.poseidoninc.poseidon.controller;

import com.poseidoninc.poseidon.domain.Rating;
import com.poseidoninc.poseidon.service.RatingService;
import com.poseidoninc.poseidon.service.RequestService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;

/**
 * RatingController class handles HTTP requests related to Rating management.
 *
 * @author olivier morel
 */
@Controller
@AllArgsConstructor
@Slf4j
public class RatingController {
    
	private final RatingService ratingService;
    private final RequestService requestService;

    @GetMapping("/rating/list")
    public String home(Model model, WebRequest request) throws UnexpectedRollbackException {
		Pageable pageRequest = Pageable.unpaged();
        Page<Rating> pageRating;
        try {
            pageRating = ratingService.getRatings(pageRequest);
        } catch(UnexpectedRollbackException urbe) {
            log.error("{} : {} ", requestService.requestToString(request), urbe.toString());
            throw new UnexpectedRollbackException("Error while getting ratings");
        }
        log.info("{} : ratings page number : {} of {}",
                requestService.requestToString(request),
                pageRating.getNumber()+1,
                pageRating.getTotalPages());
        model.addAttribute("ratings", pageRating);
		return "rating/list";
    }

    @GetMapping("/rating/add")
    public String addRatingForm(Rating rating) {
        return "rating/add";
    }

    @PostMapping("/rating/validate")
    public String validate(@Valid Rating rating, BindingResult result, WebRequest request) throws UnexpectedRollbackException {
		if (result.hasErrors()) {
            log.error("{} : rating = {} : {} ", requestService.requestToString(request), rating.toString(), result.toString());
            return "rating/add";
		}
        Rating ratingSaved;
        try  {
		    ratingSaved = ratingService.saveRating(rating);
        }  catch (UnexpectedRollbackException urbe) {
            log.error("{} : rating = {} : {} ", requestService.requestToString(request), rating.toString(), urbe.toString());
            throw new UnexpectedRollbackException("Error while saving rating");
        }
        log.info("{} : rating = {} persisted", requestService.requestToString(request), ratingSaved.toString());
        return "redirect:/rating/list";
    }

    @GetMapping("/rating/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model, WebRequest request)  throws UnexpectedRollbackException {
        Rating rating;
        try {
            rating = ratingService.getRatingById(id);
        }  catch (UnexpectedRollbackException urbe) {
            log.error("{} : rating = {} : {} ", requestService.requestToString(request), id, urbe.toString());
            throw new UnexpectedRollbackException("Error while getting rating");
        }
        log.info("{} : rating = {} gotten",  requestService.requestToString(request), rating.toString());
        model.addAttribute("rating", rating);
        return "rating/update";
    }

    @PostMapping("/rating/update")
    public String updateRating(@Valid Rating rating, BindingResult result, WebRequest request) throws UnexpectedRollbackException {
		if (result.hasErrors()) {
            log.error("{} : rating = {} : {} ", requestService.requestToString(request), rating.toString(), result.toString());
            return "rating/update";
		}
        Rating ratingUpdated;
        try {
		    ratingUpdated = ratingService.saveRating(rating);
        }  catch (UnexpectedRollbackException urbe) {
            log.error("{} : rating = {} : {} ", requestService.requestToString(request), rating.toString(), urbe.toString());
            throw new UnexpectedRollbackException("Error while saving rating");
        }
        log.info("{} : rating = {} persisted", requestService.requestToString(request), ratingUpdated.toString());
        return "redirect:/rating/list";
    }

    @GetMapping("/rating/delete/{id}")
    public String deleteRating(@PathVariable("id") Integer id, WebRequest request) throws  UnexpectedRollbackException {
        try {
            ratingService.deleteRatingById(id);
        } catch (UnexpectedRollbackException urbe) {
            log.error("{} : rating = {} : {} ", requestService.requestToString(request), id, urbe.toString());
            throw new UnexpectedRollbackException("Error while deleting rating");
        }
        log.info("{} : rating = {} deleted", requestService.requestToString(request), id);
        return "redirect:/rating/list";
    }
}
