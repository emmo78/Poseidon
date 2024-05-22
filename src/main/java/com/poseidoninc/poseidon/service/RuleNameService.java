package com.poseidoninc.poseidon.service;

import com.poseidoninc.poseidon.domain.RuleName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.UnexpectedRollbackException;

/**
 * Service for User
 *
 * @author olivier morel
 */
public interface RuleNameService {
	RuleName getRuleNameById(Integer id) throws UnexpectedRollbackException;
	Page<RuleName> getRuleNames(Pageable pageRequest) throws UnexpectedRollbackException;
	RuleName saveRuleName(RuleName ruleName) throws UnexpectedRollbackException;
	void deleteRuleNameById(Integer id) throws UnexpectedRollbackException;
}