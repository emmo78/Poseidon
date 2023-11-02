package com.poseidoninc.poseidon.controller;

import com.poseidoninc.poseidon.domain.BidList;
import com.poseidoninc.poseidon.service.BidListService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
public class BidListController {
	
	private final BidListService bidListService;

    @GetMapping("/bidList/list")
    public String home(Model model, WebRequest request) throws UnexpectedRollbackException {
		Pageable pageRequest = Pageable.unpaged();
		model.addAttribute("bidLists", bidListService.getBidLists(pageRequest, request));
       return "bidList/list";
    }

    @GetMapping("/bidList/add")
    public String addBidForm(BidList bidList) {
        return "bidList/add";
    }

    @PostMapping("/bidList/validate")
    public String validate(@Valid BidList bidList, BindingResult result, WebRequest request) throws UnexpectedRollbackException {
		if (result.hasErrors()) {
			return "bidList/add";
		}
		bidListService.saveBidList(bidList, request);
		return "redirect:/bidList/list";
    }

    @GetMapping("/bidList/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model, WebRequest request) throws UnexpectedRollbackException {
		BidList bidList = bidListService.getBidListById(id, request);
		model.addAttribute("bidList", bidList);
        return "bidList/update";
    }

    @PostMapping("/bidList/update")
    public String updateBid(@Valid BidList bidList, BindingResult result, WebRequest request) throws UnexpectedRollbackException {
		if (result.hasErrors()) {
			return "bidList/update";
		}
		bidListService.saveBidList(bidList, request);
        return "redirect:/bidList/list";
    }

    @GetMapping("/bidList/delete/{id}")
    public String deleteBid(@PathVariable("id") Integer id, WebRequest request) throws  UnexpectedRollbackException {
		bidListService.deleteBidListById(id, request);
        return "redirect:/bidList/list";
    }   
}
