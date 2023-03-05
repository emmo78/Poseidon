package com.poseidoninc.poseidon.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.RuleName;
import com.poseidoninc.poseidon.exception.ResourceConflictException;
import com.poseidoninc.poseidon.exception.ResourceNotFoundException;

public class RuleNameServiceImpl implements RuleNameService {

	@Override
	public RuleName getRuleNameById(Integer id, WebRequest request)
			throws ResourceNotFoundException, IllegalArgumentException, UnexpectedRollbackException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<RuleName> getRuleNames(Pageable pageRequest, WebRequest request) throws UnexpectedRollbackException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RuleName saveRuleName(RuleName ruleName, WebRequest request)
			throws ResourceConflictException, ResourceNotFoundException, UnexpectedRollbackException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteRuleNameById(Integer id, WebRequest request) throws UnexpectedRollbackException {
		// TODO Auto-generated method stub

	}

}
