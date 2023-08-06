package com.poseidoninc.poseidon.services;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.BidList;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import com.poseidoninc.poseidon.repositories.BidListRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class BidListServiceImpl implements BidListService {

	private final BidListRepository bidListRepository;
	private final RequestService requestService;

	
	@Override
	@Transactional(readOnly = true, rollbackFor = {ResourceNotFoundException.class, InvalidDataAccessApiUsageException.class, UnexpectedRollbackException.class})
	public BidList getBidListById(Integer bidListId, WebRequest request) throws ResourceNotFoundException, InvalidDataAccessApiUsageException, UnexpectedRollbackException {
		BidList bidList = null;
		try {
			//Throws ResourceNotFoundException | InvalidDataAccessApiUsageException
			bidList = bidListRepository.findById(bidListId).orElseThrow(() -> new ResourceNotFoundException("BidList not found"));
		} catch(InvalidDataAccessApiUsageException idaaue) {
			log.error("{} : {} ", requestService.requestToString(request), idaaue.toString());
			throw new InvalidDataAccessApiUsageException ("Id must not be null");
		} catch(ResourceNotFoundException  rnfe) {
			log.error("{} : bidList={} : {} ", requestService.requestToString(request), bidListId, rnfe.toString());
			throw new ResourceNotFoundException(rnfe.getMessage());
		} catch (Exception e) {
			log.error("{} : bidList={} : {} ", requestService.requestToString(request), bidListId, e.toString());
			throw new UnexpectedRollbackException("Error while getting bidlist");
		}
		log.info("{} : bidList={} gotten",  requestService.requestToString(request), bidList.getBidListId());
		return bidList;
	}

	@Override
	@Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
	public Page<BidList> getBidLists(Pageable pageRequest, WebRequest request) throws UnexpectedRollbackException {
		Page<BidList> pageBidList = null;
		try {
			//throws NullPointerException if pageRequest is null
			pageBidList = bidListRepository.findAll(pageRequest);
		} catch(NullPointerException npe) {
			log.error("{} : {} ", requestService.requestToString(request), npe.toString());
			throw new UnexpectedRollbackException("Error while getting BidLists");
		} catch(Exception e) {
			log.error("{} : {} ", requestService.requestToString(request), e.toString());
			throw new UnexpectedRollbackException("Error while getting BidLists");
		}
		log.info("{} : bidLists page number : {} of {}",
			requestService.requestToString(request),
			pageBidList.getNumber()+1,
			pageBidList.getTotalPages());
		return pageBidList;
	}

	@Override
	@Transactional(rollbackFor = UnexpectedRollbackException.class)
	public BidList saveBidList(BidList bidList, WebRequest request) throws UnexpectedRollbackException {
		try {
			bidList = bidListRepository.save(bidList);
		}  catch(InvalidDataAccessApiUsageException | OptimisticLockingFailureException re) {
			log.error("{} : bidList={} : {} ", requestService.requestToString(request), bidList.getBidListId(), re.toString());
			throw new UnexpectedRollbackException("Error while saving bidList");
		} catch(Exception e) {
			log.error("{} : bidList={} : {} ", requestService.requestToString(request), bidList.getBidListId(), e.toString());
			throw new UnexpectedRollbackException("Error while saving bidList");
		}
		log.info("{} : bidList={} persisted", requestService.requestToString(request), bidList.getBidListId());
		return bidList;
	}

	@Override
	@Transactional(rollbackFor = {ResourceNotFoundException.class, UnexpectedRollbackException.class})
	public void deleteBidListById(Integer bidListId, WebRequest request) throws ResourceNotFoundException, UnexpectedRollbackException {
		try {
			bidListRepository.delete(getBidListById(bidListId, request)); //getBidListById throws ResourceNotFoundException, InvalidDataAccessApiUsageException, UnexpectedRollbackException
		} catch(InvalidDataAccessApiUsageException | OptimisticLockingFailureException re) {
			log.error("{} : bidList={} : {} ", requestService.requestToString(request), bidListId, re.toString());
			throw new UnexpectedRollbackException("Error while deleting bidList");
		} catch(ResourceNotFoundException  rnfe) {
			log.error("{} : bidList={} : {} ", requestService.requestToString(request), bidListId, rnfe.toString());
			throw new ResourceNotFoundException(rnfe.getMessage());
		} catch(Exception e) {
			log.error("{} : bidList={} : {} ", requestService.requestToString(request), bidListId, e.toString());
			throw new UnexpectedRollbackException("Error while deleting bidList");
		}
		log.info("{} : bidList={} deleted", requestService.requestToString(request), bidListId);
	}

}
