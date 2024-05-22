package com.poseidoninc.poseidon.controller.api;

import com.poseidoninc.poseidon.domain.Rating;
import com.poseidoninc.poseidon.exception.BadRequestException;
import com.poseidoninc.poseidon.service.RatingService;
import com.poseidoninc.poseidon.service.RequestService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Optional;

/**
 * ApiRatingController class represents the API endpoints (CRUD) for managing Rating.
 *
 * @author olivier morel
 */
@RestController
@AllArgsConstructor
@Slf4j
@Validated //for constraints on PathVariable
public class ApiRatingController {
    
	private final RatingService ratingService;
    private final RequestService requestService;

    @GetMapping("/api/rating/list")
    public ResponseEntity<Iterable<Rating>> getRatings(WebRequest request) throws UnexpectedRollbackException {
		Pageable pageRequest = Pageable.unpaged();
        Page<Rating> pageRating = ratingService.getRatings(pageRequest); //throws UnexpectedRollbackException
        log.info("{} : {} : ratings page number : {} of {}",
                requestService.requestToString(request),
                ((ServletWebRequest) request).getHttpMethod(),
                pageRating.getNumber()+1,
                pageRating.getTotalPages());
		return new ResponseEntity<>(pageRating, HttpStatus.OK);
    }

    @PostMapping("/api/rating/create")
    public ResponseEntity<Rating> createRating(@RequestBody Optional<@Valid Rating> optionalRating, WebRequest request) throws MethodArgumentNotValidException, BadRequestException, UnexpectedRollbackException {
		if (optionalRating.isEmpty()) {
            throw new BadRequestException("Correct request should be a json Rating body");
		}
        Rating ratingSaved = ratingService.saveRating(optionalRating.get());
        log.info("{} : {} : rating = {} persisted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), ratingSaved.toString());
        return new ResponseEntity<>(ratingSaved, HttpStatus.OK);
    }

    @GetMapping("/api/rating/update/{id}") //SQL tinyint(4) = -128 to 127 so 1 to 127 for id
    public ResponseEntity<Rating> getRatingById(@PathVariable("id") @Min(1) @Max(127) Integer id, WebRequest request)  throws ConstraintViolationException, UnexpectedRollbackException {
        Rating rating = ratingService.getRatingById(id);
        log.info("{} : {} : rating = {} gotten", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), rating.toString());
        return new ResponseEntity<>(rating, HttpStatus.OK);
    }

    @PutMapping("/api/rating/update")
    public ResponseEntity<Rating> updateRating(@RequestBody Optional<@Valid Rating> optionalRating, WebRequest request) throws MethodArgumentNotValidException, BadRequestException, UnexpectedRollbackException {
        if (optionalRating.isEmpty()) {
            throw new BadRequestException("Correct request should be a json Rating body");
        }
        Rating ratingUpdated = ratingService.saveRating(optionalRating.get());
        log.info("{} : {} : rating = {} persisted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), ratingUpdated.toString());
        return new ResponseEntity<>(ratingUpdated, HttpStatus.OK);
    }

    @DeleteMapping("/api/rating/delete/{id}") //SQL tinyint(4) = -128 to 127 so 1 to 127 for id
    public HttpStatus deleteRatingById(@PathVariable("id") @Min(1) @Max(127) Integer id, WebRequest request) throws ConstraintViolationException, UnexpectedRollbackException {
        ratingService.deleteRatingById(id);
        log.info("{} : {} : rating = {} deleted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), id);
        return HttpStatus.OK;
    }
}