package com.poseidoninc.poseidon.service;

import com.poseidoninc.poseidon.domain.BidList;
import com.poseidoninc.poseidon.repository.BidListRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation class for BidListService
 *
 * @see BidListService
 *
 * @author olivier morel
 */
@Service
@AllArgsConstructor
@Slf4j
public class BidListServiceImpl implements BidListService {

	private final BidListRepository bidListRepository;

	@Override
	@Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
	public BidList getBidListById(Integer bidListId) throws UnexpectedRollbackException {
		BidList bidList;
		try {
			//Throws ResourceNotFoundException | InvalidDataAccessApiUsageException
			bidList = bidListRepository.findById(bidListId).orElseThrow(() -> new ResourceNotFoundException("BidList not found"));
		} catch (Exception e) {
			log.error("Error while getting bidlist = {} : {} ", bidListId, e.toString());
			throw new UnexpectedRollbackException("Error while getting bidlist");
		}
		return bidList;
	}

	@Override
	@Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
	public Page<BidList> getBidLists(Pageable pageRequest) throws UnexpectedRollbackException {
		Page<BidList> pageBidList;
		try {
			//throws NullPointerException if pageRequest is null
			pageBidList = bidListRepository.findAll(pageRequest);
		} catch(Exception e) {
			log.error("Error while getting bidLists : {}", e.toString());
			throw new UnexpectedRollbackException("Error while getting bidLists");
		}
		return pageBidList;
	}

	@Override
	@Transactional(rollbackFor = UnexpectedRollbackException.class)
	public BidList saveBidList(BidList bidList) throws UnexpectedRollbackException {
		BidList bidListSaved;
		try {
			/*
			 * No need to test blank or null fields for update because constraint validation on each field
			 * @DynamicUpdate, Hibernate generates an UPDATE SQL statement that sets only columns that have changed
			 * Throws InvalidDataAccessApiUsageException | OptimisticLockingFailureException
			 */
			bidListSaved = bidListRepository.save(bidList);
		} catch(Exception e) {
			log.error("Error while saving bidList = {} : {} ", bidList.toString(), e.toString());
			throw new UnexpectedRollbackException("Error while saving bidList");
		}
		return bidListSaved;
	}

	@Override
	@Transactional(rollbackFor = UnexpectedRollbackException.class)
	public void deleteBidListById(Integer bidListId) throws UnexpectedRollbackException {
		try {
			/* getBidListById throws UnexpectedRollbackException
			 * Throws InvalidDataAccessApiUsageException | OptimisticLockingFailureException */
			bidListRepository.delete(getBidListById(bidListId));
		} catch(Exception e) {
			log.error("Error while deleting bidList = {} : {} ", bidListId, e.toString());
			throw new UnexpectedRollbackException("Error while deleting bidList");
		}
	}
}
