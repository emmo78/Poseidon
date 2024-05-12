package com.poseidoninc.poseidon.service;

import com.poseidoninc.poseidon.domain.BidList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.UnexpectedRollbackException;

public interface BidListService {
	BidList getBidListById(Integer bidListId) throws UnexpectedRollbackException;
	Page<BidList> getBidLists(Pageable pageRequest) throws UnexpectedRollbackException;
	BidList saveBidList(BidList bidList) throws UnexpectedRollbackException;
	void deleteBidListById(Integer bidListId) throws UnexpectedRollbackException;
}
