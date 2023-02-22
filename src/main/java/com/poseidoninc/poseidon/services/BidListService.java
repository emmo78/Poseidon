package com.poseidoninc.poseidon.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.BidList;
import com.poseidoninc.poseidon.exception.ResourceConflictException;
import com.poseidoninc.poseidon.exception.ResourceNotFoundException;

public interface BidListService {
	BidList getBidListById(Integer bidListId, WebRequest request) throws ResourceNotFoundException, IllegalArgumentException, UnexpectedRollbackException;
	Page<BidList> getBidLists(Pageable pageRequest, WebRequest request) throws UnexpectedRollbackException;
	BidList saveBidList(BidList bidListToSave, WebRequest request) throws ResourceConflictException, ResourceNotFoundException, UnexpectedRollbackException;
	void deleteBidListById(Integer bidListTId, WebRequest request) throws UnexpectedRollbackException;	
}
