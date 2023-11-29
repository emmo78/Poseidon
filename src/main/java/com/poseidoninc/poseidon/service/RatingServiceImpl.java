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
import org.springframework.web.context.request.WebRequest;

@Service
@AllArgsConstructor
@Slf4j
public class RatingServiceImpl implements RatingService {

	private final RatingRepository ratingRepository;
	private final RequestService requestService;

	
	@Override
	@Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
	public Rating getRatingById(Integer id, WebRequest request) throws UnexpectedRollbackException {
		Rating rating = null;
		try {
			//Throws ResourceNotFoundException | InvalidDataAccessApiUsageException
			rating = ratingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Rating not found"));
		} catch (Exception e) {
			log.error("{} : rating={} : {} ", requestService.requestToString(request), id, e.toString());
			throw new UnexpectedRollbackException("Error while getting rating");
		}
		log.info("{} : rating={} gotten",  requestService.requestToString(request), rating.toString());
		return rating;
	}

	@Override
	@Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
	public Page<Rating> getRatings(Pageable pageRequest, WebRequest request) throws UnexpectedRollbackException {
		Page<Rating> pageRating = null;
		try {
			//throws NullPointerException if pageRequest is null
			pageRating = ratingRepository.findAll(pageRequest);
		} catch(Exception e) {
			log.error("{} : {} ", requestService.requestToString(request), e.toString());
			throw new UnexpectedRollbackException("Error while getting ratings");
		}
		log.info("{} : ratings page number : {} of {}",
			requestService.requestToString(request),
			pageRating.getNumber()+1,
			pageRating.getTotalPages());
		return pageRating;
	}

	@Override
	@Transactional(rollbackFor = UnexpectedRollbackException.class)
	public Rating saveRating(Rating rating, WebRequest request) throws UnexpectedRollbackException {
		try {
			rating = ratingRepository.save(rating);
		} catch(Exception e) {
			log.error("{} : rating={} : {} ", requestService.requestToString(request), rating.toString(), e.toString());
			throw new UnexpectedRollbackException("Error while saving rating");
		}
		log.info("{} : rating={} persisted", requestService.requestToString(request), rating.toString());
		return rating;
	}

	@Override
	@Transactional(rollbackFor = UnexpectedRollbackException.class)
	public void deleteRatingById(Integer id, WebRequest request) throws UnexpectedRollbackException {
		try {
			ratingRepository.delete(getRatingById(id, request)); //getRatingById throws UnexpectedRollbackException
		} catch(Exception e) {
			log.error("{} : rating={} : {} ", requestService.requestToString(request), id, e.toString());
			throw new UnexpectedRollbackException("Error while deleting rating");
		}
		log.info("{} : rating={} deleted", requestService.requestToString(request), id);
	}
}
