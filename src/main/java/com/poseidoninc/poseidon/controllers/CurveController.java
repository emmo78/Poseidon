package com.poseidoninc.poseidon.controllers;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.CurvePoint;
import com.poseidoninc.poseidon.domain.User;
import com.poseidoninc.poseidon.services.CurvePointService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class CurveController {

	private final CurvePointService curvePointService;
	
    @RequestMapping("/curvePoint/list")
    public String home(Model model, WebRequest request){
		Pageable pageRequest = Pageable.unpaged();
		model.addAttribute("curvePoints", curvePointService.getCurvePoints(pageRequest, request));
        return "curvePoint/list";
    }

    @GetMapping("/curvePoint/add")
    public String addBCurvePoint(CurvePoint curvePoint) {
        return "curvePoint/add";
    }

    @PostMapping("/curvePoint/validate")
    public String validate(@Valid CurvePoint curvePoint, BindingResult result, WebRequest request) {
		if (result.hasErrors()) {
			return "curvePoint/add";
		}
		curvePointService.saveCurvePoint(curvePoint, request);
		return "redirect:/curvePoint/list";
    }

    @GetMapping("/curvePoint/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model, WebRequest request) {
    	CurvePoint curvePoint = curvePointService.getCurvePointById(id, request);
		model.addAttribute("curvePoint", curvePoint);
        return "curvePoint/update";
    }

    @PostMapping("/curvePoint/update")
    public String updateBid(@Valid CurvePoint curvePoint, BindingResult result, WebRequest request) {
		if (result.hasErrors()) {
			return "user/update";
		}
		curvePointService.saveCurvePoint(curvePoint, request);
        return "redirect:/curvePoint/list";
    }

    @GetMapping("/curvePoint/delete/{id}")
    public String deleteBid(@PathVariable("id") Integer id, WebRequest request) {
		curvePointService.deleteCurvePointById(id, request);
        return "redirect:/curvePoint/list";
    }
}
