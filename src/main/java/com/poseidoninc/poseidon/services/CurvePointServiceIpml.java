package com.poseidoninc.poseidon.services;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.CurvePoint;
import com.poseidoninc.poseidon.domain.User;
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
	@Transactional(readOnly = true, rollbackFor = {ResourceNotFoundException.class, IllegalArgumentException.class, UnexpectedRollbackException.class})
	public CurvePoint getCurvePointById(Integer curvePointId, WebRequest request) throws ResourceNotFoundException, IllegalArgumentException, UnexpectedRollbackException {
		CurvePoint curvePoint = null;
		try {
			//Throws ResourceNotFoundException | IllegalArgumentException
			curvePoint = curvePointRepository.findById(curvePointId).orElseThrow(() -> new ResourceNotFoundException("Curve Point not found"));
		} catch(IllegalArgumentException iae) {
			log.error("{} : {} ", requestService.requestToString(request), iae.toString());
			throw new IllegalArgumentException ("Id must not be null");
		} catch(ResourceNotFoundException  rnfe) {
			log.error("{} : curvePoint={} : {} ", requestService.requestToString(request), curvePointId, rnfe.toString());
			throw new ResourceNotFoundException(rnfe.getMessage());
		} catch (Exception e) {
			log.error("{} : curvePoint={} : {} ", requestService.requestToString(request), curvePointId, e.toString());
			throw new UnexpectedRollbackException("Error while getting curvePoint");
		}
		log.info("{} : curvePoint={} gotten",  requestService.requestToString(request), curvePoint.getId());
		return curvePoint;
	}

	@Override
	@Transactional(rollbackFor = {ResourceNotFoundException.class, ResourceConflictException.class, UnexpectedRollbackException.class})
	public Page<CurvePoint> getCurvePoints(Pageable pageRequest, WebRequest request) throws UnexpectedRollbackException {
		Page<CurvePoint> pageCurvePoints = null;
		try {
			//throws NullPointerException if pageRequest is null
			pageCurvePoints = curvePointRepository.findAll(pageRequest);
		} catch(NullPointerException npe) {
			log.error("{} : {} ", requestService.requestToString(request), npe.toString());
			throw new UnexpectedRollbackException("Error while getting CurvePoints");
		} catch(Exception e) {
			log.error("{} : {} ", requestService.requestToString(request), e.toString());
			throw new UnexpectedRollbackException("Error while getting CurvePoints");
		}
		log.info("{} : curvePoints page number : {} of {}",
			requestService.requestToString(request),
			pageCurvePoints.getNumber()+1,
			pageCurvePoints.getTotalPages());
		return pageCurvePoints;
	}

	@Override
	@Transactional(rollbackFor = {ResourceNotFoundException.class, ResourceConflictException.class, UnexpectedRollbackException.class})
	public CurvePoint saveCurvePoint(CurvePoint curvePoint, WebRequest request) throws ResourceConflictException, ResourceNotFoundException, UnexpectedRollbackException {
		Integer id = curvePoint.getId(); //can be null;
		Integer curveId = curvePoint.getCurveId();
		Integer oldCurveId = null;
		try {
			oldCurveId = getCurvePointById(id, request).getCurveId(); //throw ResourceNotFoundException, IllegalArgumentException, UnexpectedRollbackException
		} catch (IllegalArgumentException iae) {			
		} finally {
			if ((id == null || !curveId.equals(oldCurveId)) && curvePointRepository.existsByCurveId(curveId)) {
				ResourceConflictException rce = new ResourceConflictException("CurveId already exists");
				log.error("{} : curvePoint={} : {} ", requestService.requestToString(request), curveId, rce.toString());				
				throw rce;
			}
		}
		try {
			//No need to test blank or null fields for update because constraint validation on each field
			curvePoint = curvePointRepository.save(curvePoint);
		}  catch(IllegalArgumentException | OptimisticLockingFailureException re) {
			log.error("{} : curvePoint={} : {} ", requestService.requestToString(request), curvePoint.getId(), re.toString());
			throw new UnexpectedRollbackException("Error while saving curvePoint");
		} catch(Exception e) {
			log.error("{} : curvePoint={} : {} ", requestService.requestToString(request), curvePoint.getId(), e.toString());
			throw new UnexpectedRollbackException("Error while saving curvePoint");
		}
		log.info("{} : curvePoint={} persisted", requestService.requestToString(request), curvePoint.getId());
		return curvePoint;
	}

	@Override
	public void deleteCurvePointById(Integer curvePointId, WebRequest request) throws UnexpectedRollbackException {
		// TODO Auto-generated method stub

	}

}
