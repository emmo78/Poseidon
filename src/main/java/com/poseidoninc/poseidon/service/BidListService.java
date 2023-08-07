package com.poseidoninc.poseidon.service;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.BidList;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

public interface BidListService {
	BidList getBidListById(Integer bidListId, WebRequest request) throws ResourceNotFoundException, InvalidDataAccessApiUsageException, UnexpectedRollbackException;
	Page<BidList> getBidLists(Pageable pageRequest, WebRequest request) throws UnexpectedRollbackException;
	BidList saveBidList(BidList bidList, WebRequest request) throws UnexpectedRollbackException;
	void deleteBidListById(Integer bidListId, WebRequest request) throws  ResourceNotFoundException, UnexpectedRollbackException;	
}
