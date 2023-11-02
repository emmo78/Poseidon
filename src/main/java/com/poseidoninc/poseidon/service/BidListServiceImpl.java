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
import org.springframework.web.context.request.WebRequest;

@Service
@AllArgsConstructor
@Slf4j
public class BidListServiceImpl implements BidListService {

	private final BidListRepository bidListRepository;
	private final RequestService requestService;

	@Override
	@Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
	public BidList getBidListById(Integer bidListId, WebRequest request) throws UnexpectedRollbackException {
		BidList bidList;
		try {
			//Throws ResourceNotFoundException | InvalidDataAccessApiUsageException
			bidList = bidListRepository.findById(bidListId).orElseThrow(() -> new ResourceNotFoundException("BidList not found"));
		} catch (Exception e) {
			log.error("{} : bidList={} : {} ", requestService.requestToString(request), bidListId, e.toString());
			throw new UnexpectedRollbackException("Error while getting bidlist");
		}
		log.info("{} : bidList={} gotten",  requestService.requestToString(request), bidList.toString());
		return bidList;
	}

	@Override
	@Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
	public Page<BidList> getBidLists(Pageable pageRequest, WebRequest request) throws UnexpectedRollbackException {
		Page<BidList> pageBidList = null;
		try {
			//throws NullPointerException if pageRequest is null
			pageBidList = bidListRepository.findAll(pageRequest);
		} catch(Exception e) {
			log.error("{} : {} ", requestService.requestToString(request), e.toString());
			throw new UnexpectedRollbackException("Error while getting bidLists");
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
		} catch(Exception e) {
			log.error("{} : bidList={} : {} ", requestService.requestToString(request), bidList.toString(), e.toString());
			throw new UnexpectedRollbackException("Error while saving bidList");
		}
		log.info("{} : bidList={} persisted", requestService.requestToString(request), bidList.toString());
		return bidList;
	}

	@Override
	@Transactional(rollbackFor = UnexpectedRollbackException.class)
	public void deleteBidListById(Integer bidListId, WebRequest request) throws UnexpectedRollbackException {
		try {
			bidListRepository.delete(getBidListById(bidListId, request)); //getBidListById throws UnexpectedRollbackException
		} catch(Exception e) {
			log.error("{} : bidList={} : {} ", requestService.requestToString(request), bidListId, e.toString());
			throw new UnexpectedRollbackException("Error while deleting bidList");
		}
		log.info("{} : bidList={} deleted", requestService.requestToString(request), bidListId);
	}

}
