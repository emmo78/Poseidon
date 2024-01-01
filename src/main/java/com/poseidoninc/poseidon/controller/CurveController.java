package com.poseidoninc.poseidon.controller;

import com.poseidoninc.poseidon.domain.CurvePoint;
import com.poseidoninc.poseidon.service.CurvePointService;
import com.poseidoninc.poseidon.service.RequestService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

@Controller
@AllArgsConstructor
@Slf4j
public class CurveController {

	private final CurvePointService curvePointService;
    private final RequestService requestService;
	
    @RequestMapping("/curvePoint/list")
    public String home(Model model, WebRequest request) throws UnexpectedRollbackException {
		Pageable pageRequest = Pageable.unpaged();
        Page<CurvePoint> pageCurvePoint;
        try {
            pageCurvePoint = curvePointService.getCurvePoints(pageRequest);
        } catch(UnexpectedRollbackException urbe) {
            log.error("{} : {} ", requestService.requestToString(request), urbe.toString());
            throw new UnexpectedRollbackException("Error while getting curvePoints");
        }
        log.info("{} : curvePoints page number : {} of {}",
                requestService.requestToString(request),
                pageCurvePoint.getNumber()+1,
                pageCurvePoint.getTotalPages());
        model.addAttribute("curvePoints", pageCurvePoint);
        return "curvePoint/list";
    }

    @GetMapping("/curvePoint/add")
    public String addCurvePoint(CurvePoint curvePoint) {
        return "curvePoint/add";
    }

    @PostMapping("/curvePoint/validate")
    public String validate(@Valid CurvePoint curvePoint, BindingResult result, WebRequest request) throws UnexpectedRollbackException {
		if (result.hasErrors()) {
            log.error("{} : curvePoint = {} : {} ", requestService.requestToString(request), curvePoint.toString(), result.toString());
            return "curvePoint/add";
		}
        CurvePoint curvePointSaved;
        try {
            curvePointSaved = curvePointService.saveCurvePoint(curvePoint);
        } catch (DataIntegrityViolationException dive) {
            log.error("{} : curvePoint = {} : {} ", requestService.requestToString(request), curvePoint.toString(), dive.toString());
            result.addError(new FieldError("CurvePoint", "curveId", dive.getMessage()));
            return "curvePoint/add";
        } catch (UnexpectedRollbackException urbe) {
            log.error("{} : curvePoint = {} : {} ", requestService.requestToString(request), curvePoint.toString(), urbe.toString());
            throw new UnexpectedRollbackException("Error while saving curvePoint");
        }
        log.info("{} : curvePoint = {} persisted", requestService.requestToString(request), curvePointSaved.toString());
		return "redirect:/curvePoint/list";
    }

    @GetMapping("/curvePoint/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model, WebRequest request) throws UnexpectedRollbackException {
    	CurvePoint curvePoint;
        try {
            curvePoint = curvePointService.getCurvePointById(id);
        }  catch (UnexpectedRollbackException urbe) {
        log.error("{} : curvePoint = {} : {} ", requestService.requestToString(request), id, urbe.toString());
        throw new UnexpectedRollbackException("Error while getting curvePoint");
        }
        log.info("{} : curvePoint = {} gotten",  requestService.requestToString(request), curvePoint.toString());
        model.addAttribute("curvePoint", curvePoint);
        return "curvePoint/update";
    }

    @PostMapping("/curvePoint/update")
    public String updateCurvePoint(@Valid CurvePoint curvePoint, BindingResult result, WebRequest request) throws UnexpectedRollbackException {
		if (result.hasErrors()) {
            log.error("{} : curvePoint = {} : {} ", requestService.requestToString(request), curvePoint.toString(), result.toString());
            return "curvePoint/update";
		}
        CurvePoint curvePointUpdated;
        try {
            curvePointUpdated = curvePointService.saveCurvePoint(curvePoint);
        } catch (DataIntegrityViolationException dive) {
            log.error("{} : curvePoint = {} : {} ", requestService.requestToString(request), curvePoint.toString(), dive.toString());
            result.addError(new FieldError("CurvePoint", "curveId", dive.getMessage()));
            return "curvePoint/update";
        } catch (UnexpectedRollbackException urbe) {
            log.error("{} : curvePoint = {} : {} ", requestService.requestToString(request), curvePoint.toString(), urbe.toString());
            throw new UnexpectedRollbackException("Error while saving curvePoint");
        }
        log.info("{} : curvePoint = {} persisted", requestService.requestToString(request), curvePointUpdated.toString());
        return "redirect:/curvePoint/list";
    }

    @GetMapping("/curvePoint/delete/{id}")
    public String deleteCurvePoint(@PathVariable("id") Integer id, WebRequest request) throws UnexpectedRollbackException {
		try {
            curvePointService.deleteCurvePointById(id);
        } catch (UnexpectedRollbackException urbe) {
            log.error("{} : curvePoint = {} : {} ", requestService.requestToString(request), id, urbe.toString());
            throw new UnexpectedRollbackException("Error while deleting curvePoint");
        }
        log.info("{} : curvePoint = {} deleted", requestService.requestToString(request), id);
        return "redirect:/curvePoint/list";
    }
}
