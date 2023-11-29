package com.poseidoninc.poseidon.service;

import com.poseidoninc.poseidon.domain.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.WebRequest;

public interface RatingService {
	Rating getRatingById(Integer id, WebRequest request) throws UnexpectedRollbackException;
	Page<Rating> getRatings(Pageable pageRequest, WebRequest request) throws UnexpectedRollbackException;
	Rating saveRating(Rating rating, WebRequest request) throws UnexpectedRollbackException;
	void deleteRatingById(Integer id, WebRequest request) throws UnexpectedRollbackException;

}
