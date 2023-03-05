package com.poseidoninc.poseidon.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.RuleName;
import com.poseidoninc.poseidon.exception.ResourceConflictException;
import com.poseidoninc.poseidon.exception.ResourceNotFoundException;

public interface RuleNameService {
	RuleName getRuleNameById(Integer id, WebRequest request) throws ResourceNotFoundException, IllegalArgumentException, UnexpectedRollbackException;
	Page<RuleName> getRuleNames(Pageable pageRequest, WebRequest request) throws UnexpectedRollbackException;
	RuleName saveRuleName(RuleName ruleName, WebRequest request) throws ResourceConflictException, ResourceNotFoundException, UnexpectedRollbackException;
	void deleteRuleNameById(Integer id, WebRequest request) throws UnexpectedRollbackException;	
}