package com.poseidoninc.poseidon.controller;

import com.poseidoninc.poseidon.domain.Trade;
import com.poseidoninc.poseidon.service.RequestService;
import com.poseidoninc.poseidon.service.TradeService;
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
public class TradeController {
	
	private final TradeService tradeService;
    private final RequestService requestService;

    @GetMapping("/trade/list")
    public String home(Model model, WebRequest request) throws UnexpectedRollbackException {
		Pageable pageRequest = Pageable.unpaged();
        Page<Trade> pageTrade;
        try {
            pageTrade = tradeService.getTrades(pageRequest);
        } catch(UnexpectedRollbackException urbe) {
            log.error("{} : {} ", requestService.requestToString(request), urbe.toString());
            throw new UnexpectedRollbackException("Error while getting trades");
        }
        log.info("{} : trades page number : {} of {}",
                requestService.requestToString(request),
                pageTrade.getNumber()+1,
                pageTrade.getTotalPages());
        model.addAttribute("trades", pageTrade);
        return "trade/list";
    }

    @GetMapping("/trade/add")
    public String addTradeForm(Trade trade) {
        return "trade/add";
    }

    @PostMapping("/trade/validate")
    public String validate(@Valid Trade trade, BindingResult result, WebRequest request) throws UnexpectedRollbackException {
		if (result.hasErrors()) {
            log.error("{} : trade = {} : {} ", requestService.requestToString(request), trade.toString(), result.toString());
            return "trade/add";
		}
        Trade tradeSaved;
        try  {
            tradeSaved = tradeService.saveTrade(trade);
        }  catch (UnexpectedRollbackException urbe) {
            log.error("{} : trade = {} : {} ", requestService.requestToString(request), trade.toString(), urbe.toString());
            throw new UnexpectedRollbackException("Error while saving trade");
        }
        log.info("{} : trade = {} persisted", requestService.requestToString(request), tradeSaved.toString());
        return "redirect:/trade/list";

    }

    @GetMapping("/trade/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model, WebRequest request) throws UnexpectedRollbackException {
        Trade trade;
        try {
            trade = tradeService.getTradeById(id);
        }  catch (UnexpectedRollbackException urbe) {
            log.error("{} : trade = {} : {} ", requestService.requestToString(request), id, urbe.toString());
            throw new UnexpectedRollbackException("Error while getting trade");
        }
        log.info("{} : trade = {} gotten",  requestService.requestToString(request), trade.toString());
        model.addAttribute("trade", trade);
        return "trade/update";
    }

    @PostMapping("/trade/update")
    public String updateTrade(@Valid Trade trade, BindingResult result, WebRequest request) throws UnexpectedRollbackException {
		if (result.hasErrors()) {
            log.error("{} : trade = {} : {} ", requestService.requestToString(request), trade.toString(), result.toString());
			return "trade/update";
		}
        Trade tradeUpdated;
        try {
            tradeUpdated = tradeService.saveTrade(trade);
        }  catch (UnexpectedRollbackException urbe) {
            log.error("{} : trade = {} : {} ", requestService.requestToString(request), trade.toString(), urbe.toString());
            throw new UnexpectedRollbackException("Error while saving trade");
        }
        log.info("{} : trade = {} persisted", requestService.requestToString(request), tradeUpdated.toString());
        return "redirect:/trade/list";
    }

    @GetMapping("/trade/delete/{id}")
    public String deleteTrade(@PathVariable("id") Integer id, WebRequest request) throws UnexpectedRollbackException {
        try {
            tradeService.deleteTradeById(id);
        } catch (UnexpectedRollbackException urbe) {
            log.error("{} : trade = {} : {} ", requestService.requestToString(request), id, urbe.toString());
            throw new UnexpectedRollbackException("Error while deleting trade");
        }
        log.info("{} : trade = {} deleted", requestService.requestToString(request), id);
        return "redirect:/trade/list";
    }   
}
