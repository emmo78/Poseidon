package com.poseidoninc.poseidon.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.Rating;
import com.poseidoninc.poseidon.exception.ResourceNotFoundException;

public interface RatingService {
	Rating getRatingById(Integer id, WebRequest request) throws ResourceNotFoundException, IllegalArgumentException, UnexpectedRollbackException;
	Page<Rating> getRatings(Pageable pageRequest, WebRequest request) throws UnexpectedRollbackException;
	Rating saveRating(Rating rating, WebRequest request) throws UnexpectedRollbackException;
	void deleteRatingById(Integer id, WebRequest request) throws ResourceNotFoundException, UnexpectedRollbackException;	

}
