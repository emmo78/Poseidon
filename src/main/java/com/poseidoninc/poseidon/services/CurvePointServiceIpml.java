package com.poseidoninc.poseidon.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.CurvePoint;
import com.poseidoninc.poseidon.exception.ResourceConflictException;
import com.poseidoninc.poseidon.exception.ResourceNotFoundException;
import com.poseidoninc.poseidon.repositories.BidListRepository;
import com.poseidoninc.poseidon.repositories.CurvePointRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class CurvePointServiceIpml implements CurvePointService {
	
	private final CurvePointRepository curvePointRepository;
	private final RequestService requestService;

	@Override
	public CurvePoint getCurvePointById(Integer curvePointId, WebRequest request) throws ResourceNotFoundException, IllegalArgumentException, UnexpectedRollbackException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<CurvePoint> getCurvePoints(Pageable pageRequest, WebRequest request)
			throws UnexpectedRollbackException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CurvePoint saveCurvePoint(CurvePoint curvePointToSave, WebRequest request)
			throws ResourceConflictException, ResourceNotFoundException, UnexpectedRollbackException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteCurvePointById(Integer curvePointId, WebRequest request) throws UnexpectedRollbackException {
		// TODO Auto-generated method stub

	}

}
