package com.poseidoninc.poseidon.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.CurvePoint;
import com.poseidoninc.poseidon.exception.ResourceConflictException;
import com.poseidoninc.poseidon.exception.ResourceNotFoundException;

public interface CurvePointService {
	CurvePoint getCurvePointById(Integer curvePointId, WebRequest request) throws ResourceNotFoundException, IllegalArgumentException, UnexpectedRollbackException;
	Page<CurvePoint> getCurvePoints(Pageable pageRequest, WebRequest request) throws UnexpectedRollbackException;
	CurvePoint saveCurvePoint(CurvePoint curvePoint, WebRequest request) throws ResourceConflictException, ResourceNotFoundException, UnexpectedRollbackException;
	void deleteCurvePointById(Integer curvePointId, WebRequest request) throws UnexpectedRollbackException;	
}
