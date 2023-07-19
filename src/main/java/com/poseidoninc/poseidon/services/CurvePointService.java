package com.poseidoninc.poseidon.services;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.CurvePoint;
import com.poseidoninc.poseidon.exception.ResourceConflictException;
import com.poseidoninc.poseidon.exception.ResourceNotFoundException;

public interface CurvePointService {
	CurvePoint getCurvePointById(Integer id, WebRequest request) throws ResourceNotFoundException, IllegalArgumentException, UnexpectedRollbackException;
	Page<CurvePoint> getCurvePoints(Pageable pageRequest, WebRequest request) throws UnexpectedRollbackException;
	CurvePoint saveCurvePoint(CurvePoint curvePoint, WebRequest request) throws DataIntegrityViolationException, UnexpectedRollbackException;
	void deleteCurvePointById(Integer id, WebRequest request) throws ResourceNotFoundException, UnexpectedRollbackException;	
}
