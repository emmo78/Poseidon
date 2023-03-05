package com.poseidoninc.poseidon.services;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.RuleName;
import com.poseidoninc.poseidon.exception.ResourceNotFoundException;
import com.poseidoninc.poseidon.repositories.RuleNameRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class RuleNameServiceImpl implements RuleNameService {

	private final RuleNameRepository ruleNameRepository;
	private final RequestService requestService;

	
	@Override
	@Transactional(readOnly = true, rollbackFor = {ResourceNotFoundException.class, IllegalArgumentException.class, UnexpectedRollbackException.class})
	public RuleName getRuleNameById(Integer id, WebRequest request) throws ResourceNotFoundException, IllegalArgumentException, UnexpectedRollbackException {
		RuleName ruleName = null;
		try {
			//Throws ResourceNotFoundException | IllegalArgumentException
			ruleName = ruleNameRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("RuleName not found"));
		} catch(IllegalArgumentException iae) {
			log.error("{} : {} ", requestService.requestToString(request), iae.toString());
			throw new IllegalArgumentException ("Id must not be null");
		} catch(ResourceNotFoundException  rnfe) {
			log.error("{} : ruleName={} : {} ", requestService.requestToString(request), id, rnfe.toString());
			throw new ResourceNotFoundException(rnfe.getMessage());
		} catch (Exception e) {
			log.error("{} : ruleName={} : {} ", requestService.requestToString(request), id, e.toString());
			throw new UnexpectedRollbackException("Error while getting ruleName");
		}
		log.info("{} : ruleName={} gotten",  requestService.requestToString(request), ruleName.getId());
		return ruleName;
	}

	@Override
	@Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
	public Page<RuleName> getRuleNames(Pageable pageRequest, WebRequest request) throws UnexpectedRollbackException {
		Page<RuleName> pageRuleName = null;
		try {
			//throws NullPointerException if pageRequest is null
			pageRuleName = ruleNameRepository.findAll(pageRequest);
		} catch(NullPointerException npe) {
			log.error("{} : {} ", requestService.requestToString(request), npe.toString());
			throw new UnexpectedRollbackException("Error while getting RuleNames");
		} catch(Exception e) {
			log.error("{} : {} ", requestService.requestToString(request), e.toString());
			throw new UnexpectedRollbackException("Error while getting RuleNames");
		}
		log.info("{} : ruleNames page number : {} of {}",
			requestService.requestToString(request),
			pageRuleName.getNumber()+1,
			pageRuleName.getTotalPages());
		return pageRuleName;
	}

	@Override
	@Transactional(rollbackFor = {UnexpectedRollbackException.class})
	public RuleName saveRuleName(RuleName ruleName, WebRequest request)
		throws UnexpectedRollbackException {
		try {
			ruleName = ruleNameRepository.save(ruleName);
		}  catch(IllegalArgumentException | OptimisticLockingFailureException re) {
			log.error("{} : ruleName={} : {} ", requestService.requestToString(request), ruleName.getId(), re.toString());
			throw new UnexpectedRollbackException("Error while saving ruleName");
		} catch(Exception e) {
			log.error("{} : ruleName={} : {} ", requestService.requestToString(request), ruleName.getId(), e.toString());
			throw new UnexpectedRollbackException("Error while saving ruleName");
		}
		log.info("{} : ruleName={} persisted", requestService.requestToString(request), ruleName.getId());
		return ruleName;
	}

	@Override
	@Transactional(rollbackFor = {IllegalArgumentException.class, ResourceNotFoundException.class, UnexpectedRollbackException.class})
	public void deleteRuleNameById(Integer id, WebRequest request) throws UnexpectedRollbackException {
		try {
			ruleNameRepository.delete(getRuleNameById(id, request)); //getRuleNameById throws ResourceNotFoundException, IllegalArgumentException, UnexpectedRollbackException
		} catch(IllegalArgumentException iae) {
			log.error("{} : ruleName={} : {} ", requestService.requestToString(request), id, iae.toString());
			throw new UnexpectedRollbackException("Error while deleting ruleName");
		} catch(ResourceNotFoundException  rnfe) {
			log.error("{} : ruleName={} : {} ", requestService.requestToString(request), id, rnfe.toString());
			throw new ResourceNotFoundException(rnfe.getMessage());
		} catch(OptimisticLockingFailureException oe) {
			log.error("{} : ruleName={} : {} ", requestService.requestToString(request), id, oe.toString());
			throw new UnexpectedRollbackException("Error while deleting ruleName");
		} catch(Exception e) {
			log.error("{} : ruleName={} : {} ", requestService.requestToString(request), id, e.toString());
			throw new UnexpectedRollbackException("Error while deleting ruleName");
		}
		log.info("{} : ruleName={} deleted", requestService.requestToString(request), id);
	}

}
