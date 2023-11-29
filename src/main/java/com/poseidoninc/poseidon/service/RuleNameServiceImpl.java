package com.poseidoninc.poseidon.service;

import com.poseidoninc.poseidon.domain.RuleName;
import com.poseidoninc.poseidon.repository.RuleNameRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

@Service
@AllArgsConstructor
@Slf4j
public class RuleNameServiceImpl implements RuleNameService {

	private final RuleNameRepository ruleNameRepository;
	private final RequestService requestService;

	
	@Override
	@Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
	public RuleName getRuleNameById(Integer id, WebRequest request) throws UnexpectedRollbackException {
		RuleName ruleName = null;
		try {
			//Throws ResourceNotFoundException | InvalidDataAccessApiUsageException
			ruleName = ruleNameRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("RuleName not found"));
		} catch (Exception e) {
			log.error("{} : ruleName={} : {} ", requestService.requestToString(request), id, e.toString());
			throw new UnexpectedRollbackException("Error while getting ruleName");
		}
		log.info("{} : ruleName={} gotten",  requestService.requestToString(request), ruleName.toString());
		return ruleName;
	}

	@Override
	@Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
	public Page<RuleName> getRuleNames(Pageable pageRequest, WebRequest request) throws UnexpectedRollbackException {
		Page<RuleName> pageRuleName = null;
		try {
			//throws NullPointerException if pageRequest is null
			pageRuleName = ruleNameRepository.findAll(pageRequest);
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
	@Transactional(rollbackFor = UnexpectedRollbackException.class)
	public RuleName saveRuleName(RuleName ruleName, WebRequest request) throws UnexpectedRollbackException {
		try {
			ruleName = ruleNameRepository.save(ruleName);
		} catch(Exception e) {
			log.error("{} : ruleName={} : {} ", requestService.requestToString(request), ruleName.toString(), e.toString());
			throw new UnexpectedRollbackException("Error while saving ruleName");
		}
		log.info("{} : ruleName={} persisted", requestService.requestToString(request), ruleName.toString());
		return ruleName;
	}

	@Override
	@Transactional(rollbackFor = UnexpectedRollbackException.class)
	public void deleteRuleNameById(Integer id, WebRequest request) throws UnexpectedRollbackException {
		try {
			ruleNameRepository.delete(getRuleNameById(id, request)); //getRuleNameById throws UnexpectedRollbackException
		} catch(Exception e) {
			log.error("{} : ruleName={} : {} ", requestService.requestToString(request), id, e.toString());
			throw new UnexpectedRollbackException("Error while deleting ruleName");
		}
		log.info("{} : ruleName={} deleted", requestService.requestToString(request), id);
	}
}
