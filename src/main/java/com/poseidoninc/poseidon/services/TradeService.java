package com.poseidoninc.poseidon.services;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.Trade;
import com.poseidoninc.poseidon.exception.ResourceNotFoundException;

public interface TradeService {
	Trade getTradeById(Integer tradeId, WebRequest request) throws ResourceNotFoundException, InvalidDataAccessApiUsageException, UnexpectedRollbackException;
	Page<Trade> getTrades(Pageable pageRequest, WebRequest request) throws UnexpectedRollbackException;
	Trade saveTrade(Trade trade, WebRequest request) throws UnexpectedRollbackException;
	void deleteTradeById(Integer tradeId, WebRequest request) throws ResourceNotFoundException, UnexpectedRollbackException;	
}
