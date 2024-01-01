package com.poseidoninc.poseidon.controller;

import com.poseidoninc.poseidon.domain.RuleName;
import com.poseidoninc.poseidon.service.RequestService;
import com.poseidoninc.poseidon.service.RuleNameService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;

@Controller
@AllArgsConstructor
@Slf4j
public class RuleNameController {
	
	private final RuleNameService ruleNameService;
    private final RequestService requestService;

    @GetMapping("/ruleName/list")
    public String home(Model model, WebRequest request) throws UnexpectedRollbackException {
		Pageable pageRequest = Pageable.unpaged();
        Page<RuleName> pageRuleName;
        try {
            pageRuleName = ruleNameService.getRuleNames(pageRequest);
        } catch(UnexpectedRollbackException urbe) {
            log.error("{} : {} ", requestService.requestToString(request), urbe.toString());
            throw new UnexpectedRollbackException("Error while getting ruleNames");
        }
        log.info("{} : ruleNames page number : {} of {}",
                requestService.requestToString(request),
                pageRuleName.getNumber()+1,
                pageRuleName.getTotalPages());
        model.addAttribute("ruleNames", pageRuleName);
       return "ruleName/list";
    }

    @GetMapping("/ruleName/add")
    public String addRuleNameForm(RuleName ruleName) {
        return "ruleName/add";
    }

    @PostMapping("/ruleName/validate")
    public String validate(@Valid RuleName ruleName, BindingResult result, WebRequest request) throws UnexpectedRollbackException {
		if (result.hasErrors()) {
            log.error("{} : ruleName = {} : {} ", requestService.requestToString(request), ruleName.toString(), result.toString());
            return "ruleName/add";
		}
        RuleName ruleNameSaved;
        try  {
            ruleNameSaved = ruleNameService.saveRuleName(ruleName);
        }  catch (UnexpectedRollbackException urbe) {
            log.error("{} : ruleName = {} : {} ", requestService.requestToString(request), ruleName.toString(), urbe.toString());
            throw new UnexpectedRollbackException("Error while saving ruleName");
        }
        log.info("{} : ruleName = {} persisted", requestService.requestToString(request), ruleNameSaved.toString());
		return "redirect:/ruleName/list";
    }

    @GetMapping("/ruleName/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model, WebRequest request) throws UnexpectedRollbackException{
        RuleName ruleName;
        try {
            ruleName = ruleNameService.getRuleNameById(id);
        }  catch (UnexpectedRollbackException urbe) {
            log.error("{} : ruleName = {} : {} ", requestService.requestToString(request), id, urbe.toString());
            throw new UnexpectedRollbackException("Error while getting ruleName");
        }
        log.info("{} : ruleName = {} gotten",  requestService.requestToString(request), ruleName.toString());
		model.addAttribute("ruleName", ruleName);
        return "ruleName/update";
    }

    @PostMapping("/ruleName/update")
    public String updateRuleName(@Valid RuleName ruleName, BindingResult result, WebRequest request) throws UnexpectedRollbackException {
		if (result.hasErrors()) {
            log.error("{} : ruleName = {} : {} ", requestService.requestToString(request), ruleName.toString(), result.toString());
			return "ruleName/update";
		}
        RuleName ruleNameUpdated;
        try {
            ruleNameUpdated = ruleNameService.saveRuleName(ruleName);
        }  catch (UnexpectedRollbackException urbe) {
            log.error("{} : ruleName = {} : {} ", requestService.requestToString(request), ruleName.toString(), urbe.toString());
            throw new UnexpectedRollbackException("Error while saving ruleName");
        }
        log.info("{} : ruleName = {} persisted", requestService.requestToString(request), ruleNameUpdated.toString());
        return "redirect:/ruleName/list";
    }

    @GetMapping("/ruleName/delete/{id}")
    public String deleteRuleName(@PathVariable("id") Integer id, WebRequest request) throws UnexpectedRollbackException {
        try {
            ruleNameService.deleteRuleNameById(id);
        } catch (UnexpectedRollbackException urbe) {
            log.error("{} : ruleName = {} : {} ", requestService.requestToString(request), id, urbe.toString());
            throw new UnexpectedRollbackException("Error while deleting ruleName");
        }
        log.info("{} : ruleName = {} deleted", requestService.requestToString(request), id);
        return "redirect:/ruleName/list";
    }   
}
