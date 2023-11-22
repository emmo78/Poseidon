package com.poseidoninc.poseidon.service;

import com.poseidoninc.poseidon.domain.Trade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.WebRequest;

public interface TradeService {
	Trade getTradeById(Integer tradeId, WebRequest request) throws UnexpectedRollbackException;
	Page<Trade> getTrades(Pageable pageRequest, WebRequest request) throws UnexpectedRollbackException;
	Trade saveTrade(Trade trade, WebRequest request) throws UnexpectedRollbackException;
	void deleteTradeById(Integer tradeId, WebRequest request) throws UnexpectedRollbackException;
}
