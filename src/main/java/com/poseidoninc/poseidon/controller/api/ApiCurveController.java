package com.poseidoninc.poseidon.controller.api;

import com.poseidoninc.poseidon.domain.CurvePoint;
import com.poseidoninc.poseidon.exception.BadRequestException;
import com.poseidoninc.poseidon.service.CurvePointService;
import com.poseidoninc.poseidon.service.RequestService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Optional;

/**
 * ApiCurveController class represents the API endpoints (CRUD) for managing Curve Point.
 *
 * @author olivier morel
 */
@RestController
@AllArgsConstructor
@Slf4j
@Validated //for constraints on PathVariable
public class ApiCurveController {

	private final CurvePointService curvePointService;
    private final RequestService requestService;
	
    @GetMapping("/api/curvePoint/list")
    public ResponseEntity<Iterable<CurvePoint>> getCurvePoints(WebRequest request) throws UnexpectedRollbackException {
		Pageable pageRequest = Pageable.unpaged();
        Page<CurvePoint> pageCurvePoint = curvePointService.getCurvePoints(pageRequest); //throws UnexpectedRollbackException
        log.info("{} : {} : curvePoints page number : {} of {}",
                requestService.requestToString(request),
                ((ServletWebRequest) request).getHttpMethod(),
                pageCurvePoint.getNumber()+1,
                pageCurvePoint.getTotalPages());
        return new ResponseEntity<>(pageCurvePoint, HttpStatus.OK);
    }

    @PostMapping("/api/curvePoint/create")
    public ResponseEntity<CurvePoint> createCurvePoint(@RequestBody Optional<@Valid CurvePoint> optionalCurvePoint, WebRequest request) throws MethodArgumentNotValidException, BadRequestException, DataIntegrityViolationException, UnexpectedRollbackException {
		if (optionalCurvePoint.isEmpty()) {
            throw new BadRequestException("Correct request should be a json CurvePoint body");
        }
        CurvePoint curvePointSaved = curvePointService.saveCurvePoint(optionalCurvePoint.get()); //Throws DataIntegrityViolationException, UnexpectedRollbackException
        log.info("{} : {} : curvePoint = {} persisted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), curvePointSaved.toString());
		return new ResponseEntity<>(curvePointSaved, HttpStatus.OK);
    }

    @GetMapping("/api/curvePoint/update/{id}") //SQL tinyint(4) = -128 to 127 so 1 to 127 for id
    public ResponseEntity<CurvePoint> getCurvePointById(@PathVariable("id") @Min(1) @Max(127) Integer id, WebRequest request) throws ConstraintViolationException, UnexpectedRollbackException {
    	CurvePoint curvePoint = curvePointService.getCurvePointById(id);
        log.info("{} : {} : curvePoint = {} gotten",  requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), curvePoint.toString());
        return new ResponseEntity<>(curvePoint, HttpStatus.OK);
    }

    @PutMapping("/api/curvePoint/update")
    public ResponseEntity<CurvePoint> updateCurvePoint(@RequestBody Optional<@Valid CurvePoint> optionalCurvePoint, WebRequest request) throws MethodArgumentNotValidException, BadRequestException, DataIntegrityViolationException, UnexpectedRollbackException {
        if (optionalCurvePoint.isEmpty()) {
            throw new BadRequestException("Correct request should be a json CurvePoint body");
        }
        CurvePoint curvePointUpdated = curvePointService.saveCurvePoint(optionalCurvePoint.get()); //Throws DataIntegrityViolationException, UnexpectedRollbackException
        log.info("{} : {} : curvePoint = {} persisted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), curvePointUpdated.toString());
        return new ResponseEntity<>(curvePointUpdated, HttpStatus.OK);
    }

    @DeleteMapping("/api/curvePoint/delete/{id}") //SQL tinyint(4) = -128 to 127 so 1 to 127 for id
    public HttpStatus deleteCurvePointById(@PathVariable("id") @Min(1) @Max(127) Integer id, WebRequest request) throws ConstraintViolationException, UnexpectedRollbackException {
        curvePointService.deleteCurvePointById(id);
        log.info("{} : {} : curvePoint = {} deleted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), id);
        return HttpStatus.OK;
    }
}