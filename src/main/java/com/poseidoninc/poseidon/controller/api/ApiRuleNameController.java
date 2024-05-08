package com.poseidoninc.poseidon.controller.api;

import com.poseidoninc.poseidon.domain.RuleName;
import com.poseidoninc.poseidon.exception.BadRequestException;
import com.poseidoninc.poseidon.service.RequestService;
import com.poseidoninc.poseidon.service.RuleNameService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@RestController
@AllArgsConstructor
@Slf4j
@Validated
public class ApiRuleNameController {
	
	private final RuleNameService ruleNameService;
    private final RequestService requestService;

    @GetMapping("/api/ruleName/list")
    public ResponseEntity<Iterable<RuleName>> getRuleNames(WebRequest request) throws UnexpectedRollbackException {
		Pageable pageRequest = Pageable.unpaged();
        Page<RuleName> pageRuleName = ruleNameService.getRuleNames(pageRequest);
        log.info("{} : {} : ruleNames page number : {} of {}",
                requestService.requestToString(request),
                ((ServletWebRequest) request).getHttpMethod(),
                pageRuleName.getNumber()+1,
                pageRuleName.getTotalPages());
       return new ResponseEntity<>(pageRuleName, HttpStatus.OK);
    }

    @PostMapping("/api/ruleName/create")
    public ResponseEntity<RuleName> createRuleName(@RequestBody Optional<@Valid  RuleName> optionalRuleName, WebRequest request) throws MethodArgumentNotValidException, BadRequestException, UnexpectedRollbackException {
        if (optionalRuleName.isEmpty()) {
            throw new BadRequestException("Correct request should be a json ruleName body");
        }
        RuleName ruleNameSaved = ruleNameService.saveRuleName(optionalRuleName.get());
        log.info("{} : {} : ruleName = {} persisted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), ruleNameSaved.toString());
		return new ResponseEntity<>(ruleNameSaved, HttpStatus.OK);
    }

    @GetMapping("/api/ruleName/update/{id}") //SQL tinyint(4) = -128 to 127 so 1 to 127 for id
    public ResponseEntity<RuleName> getRuleNameById(@PathVariable("id") @Min(1) @Max(127) Integer id, WebRequest request) throws ConstraintViolationException, UnexpectedRollbackException{
        RuleName ruleName = ruleNameService.getRuleNameById(id);
        log.info("{} : {} : ruleName = {} gotten",  requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), ruleName.toString());
        return new ResponseEntity<>(ruleName, HttpStatus.OK);
    }

    @PutMapping("/api/ruleName/update")
    public ResponseEntity<RuleName> updateRuleName(@RequestBody Optional<@Valid  RuleName> optionalRuleName, WebRequest request) throws MethodArgumentNotValidException, BadRequestException, UnexpectedRollbackException {
        if (optionalRuleName.isEmpty()) {
            throw new BadRequestException("Correct request should be a json ruleName body");
        }
        RuleName ruleNameUpdated = ruleNameService.saveRuleName(optionalRuleName.get());
        log.info("{} : {} : ruleName = {} persisted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), ruleNameUpdated.toString());
        return new ResponseEntity<>(ruleNameUpdated, HttpStatus.OK);
    }

    @DeleteMapping("/api/ruleName/delete/{id}") //SQL tinyint(4) = -128 to 127 so 1 to 127 for id
    public HttpStatus deleteRuleNameById(@PathVariable("id") @Min(1) @Max(127) Integer id, WebRequest request) throws ConstraintViolationException, UnexpectedRollbackException {
        ruleNameService.deleteRuleNameById(id);
        log.info("{} : {} : ruleName = {} deleted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), id);
        return HttpStatus.OK;
    }   
}
