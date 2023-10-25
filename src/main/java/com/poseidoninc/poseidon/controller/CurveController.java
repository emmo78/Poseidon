package com.poseidoninc.poseidon.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
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

import com.poseidoninc.poseidon.domain.CurvePoint;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import com.poseidoninc.poseidon.service.CurvePointService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class CurveController {

	private final CurvePointService curvePointService;
	
    @RequestMapping("/curvePoint/list")
    public String home(Model model, WebRequest request) throws UnexpectedRollbackException {
		Pageable pageRequest = Pageable.unpaged();
		model.addAttribute("curvePoints", curvePointService.getCurvePoints(pageRequest, request));
        return "curvePoint/list";
    }

    @GetMapping("/curvePoint/add")
    public String addCurvePoint(CurvePoint curvePoint) {
        return "curvePoint/add";
    }

    @PostMapping("/curvePoint/validate")
    public String validate(@Valid CurvePoint curvePoint, BindingResult result, WebRequest request) throws UnexpectedRollbackException {
		if (result.hasErrors()) {
			return "curvePoint/add";
		}
        try {
            curvePointService.saveCurvePoint(curvePoint, request);
        } catch (DataIntegrityViolationException dive) {
            result.addError(new FieldError("CurvePoint", "curveId", dive.getMessage()));
            return "curvePoint/add";
        }
		return "redirect:/curvePoint/list";
    }

    @GetMapping("/curvePoint/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model, WebRequest request) throws UnexpectedRollbackException {
    	CurvePoint curvePoint = curvePointService.getCurvePointById(id, request);
		model.addAttribute("curvePoint", curvePoint);
        return "curvePoint/update";
    }

    @PostMapping("/curvePoint/update")
    public String updateCurvePoint(@Valid CurvePoint curvePoint, BindingResult result, WebRequest request) throws UnexpectedRollbackException {
		if (result.hasErrors()) {
			return "curvePoint/update";
		}
        try {
            curvePointService.saveCurvePoint(curvePoint, request);
        } catch (DataIntegrityViolationException dive) {
            result.addError(new FieldError("CurvePoint", "curveId", dive.getMessage()));
            return "curvePoint/update";
        }
        return "redirect:/curvePoint/list";
    }

    @GetMapping("/curvePoint/delete/{id}")
    public String deleteCurvePoint(@PathVariable("id") Integer id, WebRequest request) throws UnexpectedRollbackException {
		curvePointService.deleteCurvePointById(id, request);
        return "redirect:/curvePoint/list";
    }
}
