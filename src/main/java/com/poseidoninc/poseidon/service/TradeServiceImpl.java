package com.poseidoninc.poseidon.service;

import com.poseidoninc.poseidon.domain.Trade;
import com.poseidoninc.poseidon.repository.TradeRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation class for TradeService
 *
 * @see TradeService
 *
 * @author olivier morel
 */
@Service
@AllArgsConstructor
@Slf4j
public class TradeServiceImpl implements TradeService {

	private final TradeRepository tradeRepository;

	@Override
	@Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
	public Trade getTradeById(Integer tradeId) throws UnexpectedRollbackException {
		Trade trade;
		try {
			//Throws ResourceNotFoundException | InvalidDataAccessApiUsageException
			trade = tradeRepository.findById(tradeId).orElseThrow(() -> new ResourceNotFoundException("Trade not found"));
		} catch (Exception e) {
			log.error("Error while getting trade = {} : {} ", tradeId, e.toString());
			throw new UnexpectedRollbackException("Error while getting trade");
		}
		return trade;
	}

	@Override
	@Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
	public Page<Trade> getTrades(Pageable pageRequest) throws UnexpectedRollbackException {
		Page<Trade> pageTrade;
		try {
			//throws NullPointerException if pageRequest is null
			pageTrade = tradeRepository.findAll(pageRequest);
		} catch(Exception e) {
			log.error("Error while getting trades : {} ", e.toString());
			throw new UnexpectedRollbackException("Error while getting trades");
		}
		return pageTrade;
	}

	@Override
	@Transactional(rollbackFor = UnexpectedRollbackException.class)
	public Trade saveTrade(Trade trade) throws UnexpectedRollbackException {
		Trade tradeSaved;
		try {
			/*
			* No need to test blank or null fields for update because constraint validation on each field
			* @DynamicUpdate, Hibernate generates an UPDATE SQL statement that sets only columns that have changed
			* Throws InvalidDataAccessApiUsageException | OptimisticLockingFailureException
			*/
			tradeSaved = tradeRepository.save(trade);
		} catch(Exception e) {
			log.error("Error while saving trade = {} : {} ", trade.toString(), e.toString());
			throw new UnexpectedRollbackException("Error while saving trade");
		}
		return tradeSaved;
	}

	@Override
	@Transactional(rollbackFor = UnexpectedRollbackException.class)
	public void deleteTradeById(Integer tradeId) throws UnexpectedRollbackException {
		try {
			/* getTradeById throws UnexpectedRollbackException
			 * Throws InvalidDataAccessApiUsageException | OptimisticLockingFailureException */
			tradeRepository.delete(getTradeById(tradeId));
		} catch(Exception e) {
			log.error("Error while deleting trade = {} : {} ", tradeId, e.toString());
			throw new UnexpectedRollbackException("Error while deleting trade");
		}
	}
}
