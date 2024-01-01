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

@Service
@AllArgsConstructor
@Slf4j
public class RuleNameServiceImpl implements RuleNameService {

	private final RuleNameRepository ruleNameRepository;
	
	@Override
	@Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
	public RuleName getRuleNameById(Integer id) throws UnexpectedRollbackException {
		RuleName ruleName;
		try {
			//Throws ResourceNotFoundException | InvalidDataAccessApiUsageException
			ruleName = ruleNameRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("RuleName not found"));
		} catch (Exception e) {
			log.error("Error while getting ruleName = {} : {} ", id, e.toString());
			throw new UnexpectedRollbackException("Error while getting ruleName");
		}
		return ruleName;
	}

	@Override
	@Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
	public Page<RuleName> getRuleNames(Pageable pageRequest) throws UnexpectedRollbackException {
		Page<RuleName> pageRuleName = null;
		try {
			//throws NullPointerException if pageRequest is null
			pageRuleName = ruleNameRepository.findAll(pageRequest);
		} catch(Exception e) {
			log.error("Error while getting ruleNames : {} ", e.toString());
			throw new UnexpectedRollbackException("Error while getting ruleNames");
		}
		return pageRuleName;
	}

	@Override
	@Transactional(rollbackFor = UnexpectedRollbackException.class)
	public RuleName saveRuleName(RuleName ruleName) throws UnexpectedRollbackException {
		RuleName ruleNameSaved;
		try {
			ruleNameSaved = ruleNameRepository.save(ruleName);
		} catch(Exception e) {
			log.error("Error while saving ruleName = {} : {} ", ruleName.toString(), e.toString());
			throw new UnexpectedRollbackException("Error while saving ruleName");
		}
		return ruleName;
	}

	@Override
	@Transactional(rollbackFor = UnexpectedRollbackException.class)
	public void deleteRuleNameById(Integer id) throws UnexpectedRollbackException {
		try {
			ruleNameRepository.delete(getRuleNameById(id)); //getRuleNameById throws UnexpectedRollbackException
		} catch(Exception e) {
			log.error("Error while deleting ruleName = {} : {} ", id, e.toString());
			throw new UnexpectedRollbackException("Error while deleting ruleName");
		}
	}
}
