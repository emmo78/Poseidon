package com.poseidoninc.poseidon.services;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.Trade;
import com.poseidoninc.poseidon.exception.ResourceNotFoundException;
import com.poseidoninc.poseidon.repositories.TradeRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class TradeServiceImpl implements TradeService {

	private final TradeRepository tradeRepository;
	private final RequestService requestService;

	
	@Override
	@Transactional(readOnly = true, rollbackFor = {ResourceNotFoundException.class, IllegalArgumentException.class, UnexpectedRollbackException.class})
	public Trade getTradeById(Integer tradeId, WebRequest request) throws ResourceNotFoundException, IllegalArgumentException, UnexpectedRollbackException {
		Trade trade = null;
		try {
			//Throws ResourceNotFoundException | IllegalArgumentException
			trade = tradeRepository.findById(tradeId).orElseThrow(() -> new ResourceNotFoundException("Trade not found"));
		} catch(IllegalArgumentException iae) {
			log.error("{} : {} ", requestService.requestToString(request), iae.toString());
			throw new IllegalArgumentException ("Id must not be null");
		} catch(ResourceNotFoundException  rnfe) {
			log.error("{} : trade={} : {} ", requestService.requestToString(request), tradeId, rnfe.toString());
			throw new ResourceNotFoundException(rnfe.getMessage());
		} catch (Exception e) {
			log.error("{} : trade={} : {} ", requestService.requestToString(request), tradeId, e.toString());
			throw new UnexpectedRollbackException("Error while getting trade");
		}
		log.info("{} : trade={} gotten",  requestService.requestToString(request), trade.getTradeId());
		return trade;
	}

	@Override
	@Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
	public Page<Trade> getTrades(Pageable pageRequest, WebRequest request) throws UnexpectedRollbackException {
		Page<Trade> pageTrade = null;
		try {
			//throws NullPointerException if pageRequest is null
			pageTrade = tradeRepository.findAll(pageRequest);
		} catch(NullPointerException npe) {
			log.error("{} : {} ", requestService.requestToString(request), npe.toString());
			throw new UnexpectedRollbackException("Error while getting trades");
		} catch(Exception e) {
			log.error("{} : {} ", requestService.requestToString(request), e.toString());
			throw new UnexpectedRollbackException("Error while getting trades");
		}
		log.info("{} : trades page number : {} of {}",
			requestService.requestToString(request),
			pageTrade.getNumber()+1,
			pageTrade.getTotalPages());
		return pageTrade;
	}

	@Override
	@Transactional(rollbackFor = {UnexpectedRollbackException.class})
	public Trade saveTrade(Trade trade, WebRequest request)
		throws UnexpectedRollbackException {
		try {
			trade = tradeRepository.save(trade);
		}  catch(IllegalArgumentException | OptimisticLockingFailureException re) {
			log.error("{} : trade={} : {} ", requestService.requestToString(request), trade.getTradeId(), re.toString());
			throw new UnexpectedRollbackException("Error while saving trade");
		} catch(Exception e) {
			log.error("{} : trade={} : {} ", requestService.requestToString(request), trade.getTradeId(), e.toString());
			throw new UnexpectedRollbackException("Error while saving trade");
		}
		log.info("{} : trade={} persisted", requestService.requestToString(request), trade.getTradeId());
		return trade;
	}

	@Override
	@Transactional(rollbackFor = {IllegalArgumentException.class, ResourceNotFoundException.class, UnexpectedRollbackException.class})
	public void deleteTradeById(Integer tradeId, WebRequest request) throws UnexpectedRollbackException {
		try {
			tradeRepository.delete(getTradeById(tradeId, request)); //getTradeById throws ResourceNotFoundException, IllegalArgumentException, UnexpectedRollbackException
		} catch(IllegalArgumentException iae) {
			log.error("{} : trade={} : {} ", requestService.requestToString(request), tradeId, iae.toString());
			throw new UnexpectedRollbackException("Error while deleting trade");
		} catch(ResourceNotFoundException  rnfe) {
			log.error("{} : trade={} : {} ", requestService.requestToString(request), tradeId, rnfe.toString());
			throw new ResourceNotFoundException(rnfe.getMessage());
		} catch(OptimisticLockingFailureException oe) {
			log.error("{} : trade={} : {} ", requestService.requestToString(request), tradeId, oe.toString());
			throw new UnexpectedRollbackException("Error while deleting trade");
		} catch(Exception e) {
			log.error("{} : trade={} : {} ", requestService.requestToString(request), tradeId, e.toString());
			throw new UnexpectedRollbackException("Error while deleting trade");
		}
		log.info("{} : trade={} deleted", requestService.requestToString(request), tradeId);
	}

}
