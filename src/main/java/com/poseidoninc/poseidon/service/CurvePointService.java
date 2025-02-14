package com.poseidoninc.poseidon.service;

import com.poseidoninc.poseidon.domain.CurvePoint;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.UnexpectedRollbackException;

/**
 * Service for CurvePoint
 *
 * @author olivier morel
 */
public interface CurvePointService {
	CurvePoint getCurvePointById(Integer id) throws UnexpectedRollbackException;
	Page<CurvePoint> getCurvePoints(Pageable pageRequest) throws UnexpectedRollbackException;

	/**
	 *
	 * Throws DataIntegrityViolationException if unique violation on CurveId
	 *
	 * @param curvePoint
	 * @return
	 * @throws DataIntegrityViolationException
	 * @throws UnexpectedRollbackException
	 */
	CurvePoint saveCurvePoint(CurvePoint curvePoint) throws DataIntegrityViolationException, UnexpectedRollbackException;
	void deleteCurvePointById(Integer id) throws UnexpectedRollbackException;
}
