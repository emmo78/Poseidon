package com.poseidoninc.poseidon.service;

import com.poseidoninc.poseidon.domain.Rating;
import com.poseidoninc.poseidon.repository.RatingRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation class for RatingService
 *
 * @see RatingService
 *
 * @author olivier morel
 */
@Service
@AllArgsConstructor
@Slf4j
public class RatingServiceImpl implements RatingService {

	private final RatingRepository ratingRepository;
	
	@Override
	@Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
	public Rating getRatingById(Integer id) throws UnexpectedRollbackException {
		Rating rating;
		try {
			//Throws ResourceNotFoundException | InvalidDataAccessApiUsageException
			rating = ratingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Rating not found"));
		} catch (Exception e) {
			log.error("Error while getting rating = {} : {} ", id, e.toString());
			throw new UnexpectedRollbackException("Error while getting rating");
		}
		return rating;
	}

	@Override
	@Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
	public Page<Rating> getRatings(Pageable pageRequest) throws UnexpectedRollbackException {
		Page<Rating> pageRating;
		try {
			//throws NullPointerException if pageRequest is null
			pageRating = ratingRepository.findAll(pageRequest);
		} catch(Exception e) {
			log.error("Error while getting ratings : {} ", e.toString());
			throw new UnexpectedRollbackException("Error while getting ratings");
		}
		return pageRating;
	}

	@Override
	@Transactional(rollbackFor = UnexpectedRollbackException.class)
	public Rating saveRating(Rating rating) throws UnexpectedRollbackException {
		Rating ratingSaved;
		try {
			/*
			 * No need to test blank or null fields for update because constraint validation on each field
			 * @DynamicUpdate, Hibernate generates an UPDATE SQL statement that sets only columns that have changed
			 * Throws InvalidDataAccessApiUsageException | OptimisticLockingFailureException
			 */
			ratingSaved = ratingRepository.save(rating);
		} catch(Exception e) {
			log.error("Error while saving rating = {} : {} ", rating.toString(), e.toString());
			throw new UnexpectedRollbackException("Error while saving rating");
		}
		return ratingSaved;
	}

	@Override
	@Transactional(rollbackFor = UnexpectedRollbackException.class)
	public void deleteRatingById(Integer id) throws UnexpectedRollbackException {
		try {
			/* getRatingById throws UnexpectedRollbackException
			 * Throws InvalidDataAccessApiUsageException | OptimisticLockingFailureException */
			ratingRepository.delete(getRatingById(id));
		} catch(Exception e) {
			log.error("Error while deleting rating = {} : {} ", id, e.toString());
			throw new UnexpectedRollbackException("Error while deleting rating");
		}
	}
}
