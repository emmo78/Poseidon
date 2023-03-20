package com.poseidoninc.poseidon.controllers;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.RuleName;
import com.poseidoninc.poseidon.exception.ResourceNotFoundException;
import com.poseidoninc.poseidon.services.RuleNameService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class RuleNameController {
	
	private final RuleNameService ruleNameService;

    @GetMapping("/ruleName/list")
    public String home(Model model, WebRequest request) throws UnexpectedRollbackException {
		Pageable pageRequest = Pageable.unpaged();
		model.addAttribute("ruleNames", ruleNameService.getRuleNames(pageRequest, request));
       return "ruleName/list";
    }

    @GetMapping("/ruleName/add")
    public String addRuleNameForm(RuleName ruleName) {
        return "ruleName/add";
    }

    @PostMapping("/ruleName/validate")
    public String validate(@Valid RuleName ruleName, BindingResult result, WebRequest request) throws UnexpectedRollbackException {
		if (result.hasErrors()) {
			return "ruleName/add";
		}
		ruleNameService.saveRuleName(ruleName, request);
		return "redirect:/ruleName/list";
    }

    @GetMapping("/ruleName/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model, WebRequest request) throws ResourceNotFoundException, IllegalArgumentException, UnexpectedRollbackException{
		RuleName ruleName = ruleNameService.getRuleNameById(id, request);
		model.addAttribute("ruleName", ruleName);
        return "ruleName/update";
    }

    @PostMapping("/ruleName/update")
    public String updateRuleName(@Valid RuleName ruleName, BindingResult result, WebRequest request) throws UnexpectedRollbackException {
		if (result.hasErrors()) {
			return "ruleName/update";
		}
		ruleNameService.saveRuleName(ruleName, request);
        return "redirect:/ruleName/list";
    }

    @GetMapping("/ruleName/delete/{id}")
    public String deleteRuleName(@PathVariable("id") Integer id, WebRequest request) throws ResourceNotFoundException, UnexpectedRollbackException {
		ruleNameService.deleteRuleNameById(id, request);
        return "redirect:/ruleName/list";
    }   
}
