package com.poseidoninc.poseidon.service;

import com.poseidoninc.poseidon.domain.BidList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.WebRequest;

public interface BidListService {
	BidList getBidListById(Integer bidListId, WebRequest request) throws UnexpectedRollbackException;
	Page<BidList> getBidLists(Pageable pageRequest, WebRequest request) throws UnexpectedRollbackException;
	BidList saveBidList(BidList bidList, WebRequest request) throws UnexpectedRollbackException;
	void deleteBidListById(Integer bidListId, WebRequest request) throws UnexpectedRollbackException;
}
