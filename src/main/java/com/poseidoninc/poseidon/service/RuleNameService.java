package com.poseidoninc.poseidon.service;

import com.poseidoninc.poseidon.domain.RuleName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.WebRequest;

public interface RuleNameService {
	RuleName getRuleNameById(Integer id, WebRequest request) throws UnexpectedRollbackException;
	Page<RuleName> getRuleNames(Pageable pageRequest, WebRequest request) throws UnexpectedRollbackException;
	RuleName saveRuleName(RuleName ruleName, WebRequest request) throws UnexpectedRollbackException;
	void deleteRuleNameById(Integer id, WebRequest request) throws UnexpectedRollbackException;
}