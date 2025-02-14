package com.poseidoninc.poseidon.service;

import com.poseidoninc.poseidon.domain.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.UnexpectedRollbackException;

/**
 * Service for Rating
 *
 * @author olivier morel
 */
public interface RatingService {
	Rating getRatingById(Integer id) throws UnexpectedRollbackException;
	Page<Rating> getRatings(Pageable pageRequest) throws UnexpectedRollbackException;
	Rating saveRating(Rating rating) throws UnexpectedRollbackException;
	void deleteRatingById(Integer id) throws UnexpectedRollbackException;

}
