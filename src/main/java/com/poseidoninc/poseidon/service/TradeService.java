package com.poseidoninc.poseidon.service;

import com.poseidoninc.poseidon.domain.Trade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.UnexpectedRollbackException;

/**
 * Service for Trade
 *
 * @author olivier morel
 */
public interface TradeService {
	Trade getTradeById(Integer tradeId) throws UnexpectedRollbackException;
	Page<Trade> getTrades(Pageable pageRequest) throws UnexpectedRollbackException;
	Trade saveTrade(Trade trade) throws UnexpectedRollbackException;
	void deleteTradeById(Integer tradeId) throws UnexpectedRollbackException;
}
