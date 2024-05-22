package com.poseidoninc.poseidon.controller;

import com.poseidoninc.poseidon.domain.BidList;
import com.poseidoninc.poseidon.service.BidListService;
import com.poseidoninc.poseidon.service.RequestService;
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

/**
 * BidListController class handles HTTP requests related to Bid List management.
 *
 * @author olivier morel
 */
@Controller
@AllArgsConstructor
@Slf4j
public class BidListController {
	
	private final BidListService bidListService;
    private final RequestService requestService;

    @GetMapping("/bidList/list")
    public String home(Model model, WebRequest request) throws UnexpectedRollbackException {
		Pageable pageRequest = Pageable.unpaged();
        Page<BidList> pageBidList;
        try {
            pageBidList = bidListService.getBidLists(pageRequest);
        } catch(UnexpectedRollbackException urbe) {
            log.error("{} : {} ", requestService.requestToString(request), urbe.toString());
            throw new UnexpectedRollbackException("Error while getting bidLists");
        }
        log.info("{} : bidLists page number : {} of {}",
                requestService.requestToString(request),
                pageBidList.getNumber()+1,
                pageBidList.getTotalPages());
        model.addAttribute("bidLists", pageBidList);
       return "bidList/list";
    }

    @GetMapping("/bidList/add")
    public String addBidForm(BidList bidList) {
        return "bidList/add";
    }

    @PostMapping("/bidList/validate")
    public String validate(@Valid BidList bidList, BindingResult result, WebRequest request) throws UnexpectedRollbackException {
		if (result.hasErrors()) {
            log.error("{} : bidList = {} : {} ", requestService.requestToString(request), bidList.toString(), result.toString());
			return "bidList/add";
		}
        BidList bidListSaved;
		try {
            bidListSaved = bidListService.saveBidList(bidList);
        } catch (UnexpectedRollbackException urbe) {
           log.error("{} : bidList = {} : {} ", requestService.requestToString(request), bidList.toString(), urbe.toString());
           throw new UnexpectedRollbackException("Error while saving bidList");
        }
        log.info("{} : bidList = {} persisted", requestService.requestToString(request), bidListSaved.toString());
        return "redirect:/bidList/list";
    }

    @GetMapping("/bidList/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model, WebRequest request) throws UnexpectedRollbackException {
		BidList bidList;
        try {
            bidList = bidListService.getBidListById(id);
        } catch (UnexpectedRollbackException urbe) {
            log.error("{} : bidList = {} : {} ", requestService.requestToString(request), id, urbe.toString());
            throw new UnexpectedRollbackException("Error while getting bidList");
        }
        log.info("{} : bidList = {} gotten",  requestService.requestToString(request), bidList.toString());
        model.addAttribute("bidList", bidList);
        return "bidList/update";
    }

    @PostMapping("/bidList/update")
    public String updateBid(@Valid BidList bidList, BindingResult result, WebRequest request) throws UnexpectedRollbackException {
		if (result.hasErrors()) {
            log.error("{} : bidList = {} : {} ", requestService.requestToString(request), bidList.toString(), result.toString());
			return "bidList/update";
		}
        BidList bidListUpdated;
        try {
            bidListUpdated = bidListService.saveBidList(bidList);
        } catch (UnexpectedRollbackException urbe) {
            log.error("{} : bidList = {} : {} ", requestService.requestToString(request), bidList.toString(), urbe.toString());
            throw new UnexpectedRollbackException("Error while saving bidList");
        }
        log.info("{} : bidList = {} persisted", requestService.requestToString(request), bidListUpdated.toString());
        return "redirect:/bidList/list";
    }

    @GetMapping("/bidList/delete/{id}")
    public String deleteBid(@PathVariable("id") Integer id, WebRequest request) throws  UnexpectedRollbackException {
		try {
            bidListService.deleteBidListById(id);
        } catch (UnexpectedRollbackException urbe) {
            log.error("{} : bidList = {} : {} ", requestService.requestToString(request), id, urbe.toString());
            throw new UnexpectedRollbackException("Error while deleting bidList");
        }
        log.info("{} : bidList = {} deleted", requestService.requestToString(request), id);
        return "redirect:/bidList/list";
    }   
}