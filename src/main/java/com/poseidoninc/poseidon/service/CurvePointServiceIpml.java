package com.poseidoninc.poseidon.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.CurvePoint;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import com.poseidoninc.poseidon.repository.CurvePointRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class CurvePointServiceIpml implements CurvePointService {

	private final CurvePointRepository curvePointRepository;
	private final RequestService requestService;

	@Override
	@Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
	public CurvePoint getCurvePointById(Integer id, WebRequest request) throws UnexpectedRollbackException {
		CurvePoint curvePoint;
		try {
			//Throws ResourceNotFoundException | InvalidDataAccessApiUsageException
			curvePoint = curvePointRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Curve Point not found"));
		} catch (Exception e) {
			log.error("{} : curvePoint={} : {} ", requestService.requestToString(request), id, e.toString());
			throw new UnexpectedRollbackException("Error while getting curvePoint");
		}
		log.info("{} : curvePoint={} gotten",  requestService.requestToString(request), curvePoint.getId());
		return curvePoint;
	}

	@Override
	@Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
	public Page<CurvePoint> getCurvePoints(Pageable pageRequest, WebRequest request) throws UnexpectedRollbackException {
		Page<CurvePoint> pageCurvePoints;
		try {
			//throws NullPointerException if pageRequest is null
			pageCurvePoints = curvePointRepository.findAll(pageRequest);
		} catch(Exception e) {
			log.error("{} : {} ", requestService.requestToString(request), e.toString());
			throw new UnexpectedRollbackException("Error while getting curvePoints");
		}
		log.info("{} : curvePoints page number : {} of {}",
			requestService.requestToString(request),
			pageCurvePoints.getNumber()+1,
			pageCurvePoints.getTotalPages());
		return pageCurvePoints;
	}

	@Override
	@Transactional(rollbackFor = {DataIntegrityViolationException.class, UnexpectedRollbackException.class})
	public CurvePoint saveCurvePoint(CurvePoint curvePoint, WebRequest request) throws DataIntegrityViolationException, UnexpectedRollbackException {
		try {
			//No need to test blank or null fields for update because constraint validation on each field
			curvePoint = curvePointRepository.save(curvePoint);
		} catch(DataIntegrityViolationException dive) {
			log.error("{} : curvePoint={} : {} ", requestService.requestToString(request), curvePoint.toString(), dive.toString());
			throw new DataIntegrityViolationException("CurveId already exists");
		} catch(Exception e) {
			log.error("{} : curvePoint={} : {} ", requestService.requestToString(request), curvePoint.toString(), e.toString());
			throw new UnexpectedRollbackException("Error while saving curvePoint");
		}
		log.info("{} : curvePoint={} persisted", requestService.requestToString(request), curvePoint.toString());
		return curvePoint;
	}

	@Override
	@Transactional(rollbackFor = {UnexpectedRollbackException.class})
	public void deleteCurvePointById(Integer id, WebRequest request) throws UnexpectedRollbackException {
		try {
			curvePointRepository.delete(getCurvePointById(id, request)); //getCurvePointById throws ResourceNotFoundException, InvalidDataAccessApiUsageException, UnexpectedRollbackException
		} catch(Exception e) {
			log.error("{} : curvePoint={} : {} ", requestService.requestToString(request), id, e.toString());
			throw new UnexpectedRollbackException("Error while deleting curvePoint");
		}
		log.info("{} : curvePoint={} deleted", requestService.requestToString(request), id);
	}
}
