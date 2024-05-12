package com.poseidoninc.poseidon.service;

import com.poseidoninc.poseidon.domain.CurvePoint;
import com.poseidoninc.poseidon.repository.CurvePointRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class CurvePointServiceIpml implements CurvePointService {

	private final CurvePointRepository curvePointRepository;

	@Override
	@Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
	public CurvePoint getCurvePointById(Integer id) throws UnexpectedRollbackException {
		CurvePoint curvePoint;
		try {
			//Throws ResourceNotFoundException | InvalidDataAccessApiUsageException
			curvePoint = curvePointRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Curve Point not found"));
		} catch (Exception e) {
			log.error("Error while getting curvePoint = {} : {} ", id, e.toString());
			throw new UnexpectedRollbackException("Error while getting curvePoint");
		}
		return curvePoint;
	}

	@Override
	@Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
	public Page<CurvePoint> getCurvePoints(Pageable pageRequest) throws UnexpectedRollbackException {
		Page<CurvePoint> pageCurvePoint;
		try {
			//throws NullPointerException if pageRequest is null
			pageCurvePoint = curvePointRepository.findAll(pageRequest);
		} catch(Exception e) {
			log.error("Error while getting curvePoints : {}", e.toString());
			throw new UnexpectedRollbackException("Error while getting curvePoints");
		}
		return pageCurvePoint;
	}

	@Override
	@Transactional(rollbackFor = {DataIntegrityViolationException.class, UnexpectedRollbackException.class})
	public CurvePoint saveCurvePoint(CurvePoint curvePoint) throws DataIntegrityViolationException, UnexpectedRollbackException {
		CurvePoint curvePointSaved;
		try {
			//No need to test blank or null fields for update because constraint validation on each field
			curvePointSaved = curvePointRepository.save(curvePoint);
		} catch(DataIntegrityViolationException dive) {
			log.error("Error while saving curvePoint = {} : {} ", curvePoint.toString(), dive.toString());
			throw new DataIntegrityViolationException("CurveId already exists");
		} catch(Exception e) {
			log.error("Error while saving curvePoint = {} : {} ", curvePoint.toString(), e.toString());
			throw new UnexpectedRollbackException("Error while saving curvePoint");
		}
		return curvePointSaved;
	}

	@Override
	@Transactional(rollbackFor = UnexpectedRollbackException.class)
	public void deleteCurvePointById(Integer id) throws UnexpectedRollbackException {
		try {
			curvePointRepository.delete(getCurvePointById(id)); //getCurvePointById throws UnexpectedRollbackException
		} catch(Exception e) {
			log.error("Error while deleting curvePoint = {} : {} ", id, e.toString());
			throw new UnexpectedRollbackException("Error while deleting curvePoint");
		}
	}
}
