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

import com.poseidoninc.poseidon.domain.Trade;
import com.poseidoninc.poseidon.exception.ResourceNotFoundException;
import com.poseidoninc.poseidon.services.TradeService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class TradeController {
	
	private final TradeService tradeService;

    @GetMapping("/trade/list")
    public String home(Model model, WebRequest request) throws UnexpectedRollbackException {
		Pageable pageRequest = Pageable.unpaged();
		model.addAttribute("trades", tradeService.getTrades(pageRequest, request));
       return "trade/list";
    }

    @GetMapping("/trade/add")
    public String addBidForm(Trade trade) {
        return "trade/add";
    }

    @PostMapping("/trade/validate")
    public String validate(@Valid Trade trade, BindingResult result, WebRequest request) throws UnexpectedRollbackException {
		if (result.hasErrors()) {
			return "trade/add";
		}
		tradeService.saveTrade(trade, request);
		return "redirect:/trade/list";
    }

    @GetMapping("/trade/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model, WebRequest request) throws ResourceNotFoundException, IllegalArgumentException, UnexpectedRollbackException {
		Trade trade = tradeService.getTradeById(id, request);
		model.addAttribute("trade", trade);
        return "trade/update";
    }

    @PostMapping("/trade/update")
    public String updateBid(@Valid Trade trade, BindingResult result, WebRequest request) throws UnexpectedRollbackException {
		if (result.hasErrors()) {
			return "trade/update";
		}
		tradeService.saveTrade(trade, request);
        return "redirect:/trade/list";
    }

    @GetMapping("/trade/delete/{id}")
    public String deleteBid(@PathVariable("id") Integer id, WebRequest request) throws ResourceNotFoundException, UnexpectedRollbackException {
		tradeService.deleteTradeById(id, request);
        return "redirect:/trade/list";
    }   
}
