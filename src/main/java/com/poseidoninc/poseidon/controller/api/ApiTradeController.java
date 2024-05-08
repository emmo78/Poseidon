package com.poseidoninc.poseidon.controller.api;

import com.poseidoninc.poseidon.domain.Trade;
import com.poseidoninc.poseidon.exception.BadRequestException;
import com.poseidoninc.poseidon.service.RequestService;
import com.poseidoninc.poseidon.service.TradeService;
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
public class ApiTradeController {
	
	private final TradeService tradeService;
    private final RequestService requestService;

    @GetMapping("/api/trade/list")
    public ResponseEntity<Iterable<Trade>> getTrades(WebRequest request) throws UnexpectedRollbackException {
		Pageable pageRequest = Pageable.unpaged();
        Page<Trade> pageTrade = tradeService.getTrades(pageRequest);
        log.info("{} : {} : trades page number : {} of {}",
                requestService.requestToString(request),
                ((ServletWebRequest) request).getHttpMethod(),
                pageTrade.getNumber()+1,
                pageTrade.getTotalPages());
        return new ResponseEntity<>(pageTrade, HttpStatus.OK);
    }

    @PostMapping("/api/trade/create")
    public ResponseEntity<Trade> createTrade(@RequestBody Optional<@Valid Trade> optionalTrade, WebRequest request) throws MethodArgumentNotValidException, BadRequestException, UnexpectedRollbackException {
        if (optionalTrade.isEmpty()) {
            throw new BadRequestException("Correct request should be a json Trade body");
        }
        Trade tradeSaved = tradeService.saveTrade(optionalTrade.get());
        log.info("{} : {} : trade = {} persisted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), tradeSaved.toString());
        return new ResponseEntity<>(tradeSaved, HttpStatus.OK);
    }

    @GetMapping("/api/trade/update/{id}") //SQL tinyint(4) = -128 to 127 so 1 to 127 for id
    public ResponseEntity<Trade> getTradeById(@PathVariable("id") @Min(1) @Max(127) Integer id, WebRequest request) throws ConstraintViolationException, UnexpectedRollbackException {
        Trade trade = tradeService.getTradeById(id);
        log.info("{} : {} : trade = {} gotten",  requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), trade.toString());
        return new ResponseEntity<>(trade, HttpStatus.OK);
    }

    @PutMapping("/api/trade/update")
    public ResponseEntity<Trade> updateTrade(@RequestBody Optional<@Valid Trade> optionalTrade, WebRequest request) throws MethodArgumentNotValidException, BadRequestException, UnexpectedRollbackException {
        if (optionalTrade.isEmpty()) {
            throw new BadRequestException("Correct request should be a json Trade body");
        }
        Trade tradeUpdated = tradeService.saveTrade(optionalTrade.get());
        log.info("{} : {} : trade = {} persisted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), tradeUpdated.toString());
        return new ResponseEntity<>(tradeUpdated, HttpStatus.OK);
    }

    @DeleteMapping("/api/trade/delete/{id}") //SQL tinyint(4) = -128 to 127 so 1 to 127 for id
    public  HttpStatus deleteTradeById(@PathVariable("id") @Min(1) @Max(127) Integer id, WebRequest request) throws ConstraintViolationException, UnexpectedRollbackException {
        tradeService.deleteTradeById(id);
        log.info("{} : {} : trade = {} deleted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), id);
        return HttpStatus.OK;
    }   
}
