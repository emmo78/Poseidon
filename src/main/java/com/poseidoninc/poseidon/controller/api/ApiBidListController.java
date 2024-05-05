package com.poseidoninc.poseidon.controller.api;

import com.poseidoninc.poseidon.domain.BidList;
import com.poseidoninc.poseidon.service.BidListService;
import com.poseidoninc.poseidon.service.RequestService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import jakarta.validation.ConstraintViolationException;
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
public class ApiBidListController {
	
	private final BidListService bidListService;
    private final RequestService requestService;

    @GetMapping("/api/bidList/list")
    public ResponseEntity<Iterable<BidList>> getBidLists(WebRequest request) throws UnexpectedRollbackException {
		Pageable pageRequest = Pageable.unpaged();
        Page<BidList> pageBidList = bidListService.getBidLists(pageRequest); //throws UnexpectedRollbackException
        log.info("{} : {} : bidLists page number : {} of {}",
                requestService.requestToString(request),
                ((ServletWebRequest) request).getHttpMethod(),
                pageBidList.getNumber()+1,
                pageBidList.getTotalPages());
        return new ResponseEntity<>(pageBidList, HttpStatus.OK);
    }

    @PostMapping("/api/bidList/create")
    public ResponseEntity<BidList>  createBidList(@RequestBody Optional<@Valid BidList> optionalBidList, WebRequest request) throws MethodArgumentNotValidException, BadRequestException, UnexpectedRollbackException {
		if (optionalBidList.isEmpty()) {
            throw new BadRequestException("Correct request should be a json BidList body");
		}
        BidList bidListSaved = bidListService.saveBidList(optionalBidList.get());
        log.info("{} : {} : bidList = {} persisted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), bidListSaved.toString());
        return new ResponseEntity<>(bidListSaved, HttpStatus.OK);
    }

    @GetMapping("/api/bidList/update/{id}")  //SQL tinyint(4) = -128 to 127 so 1 to 127 for id
    public ResponseEntity<BidList> getBidListById(@PathVariable("id") @Min(1) @Max(127) Integer id, WebRequest request) throws ConstraintViolationException, UnexpectedRollbackException {
		BidList bidList = bidListService.getBidListById(id); //Throws UnexpectedRollbackException
        log.info("{} : {} : bidList = {} gotten",  requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), bidList.toString());
        return new ResponseEntity<>(bidList, HttpStatus.OK);
    }

    @PutMapping("/api/bidList/update")
    public ResponseEntity<BidList>  updateBidList(@RequestBody Optional<@Valid BidList> optionalBidList, WebRequest request) throws MethodArgumentNotValidException, BadRequestException, UnexpectedRollbackException {
        if (optionalBidList.isEmpty()) {
            throw new BadRequestException("Correct request should be a json bidList body");
        }
        BidList bidListUpdated = bidListService.saveBidList(optionalBidList.get());
        log.info("{} : {} : bidList = {} persisted", requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), bidListUpdated.toString());
        return new ResponseEntity<>(bidListUpdated, HttpStatus.OK);
    }

    @DeleteMapping("/api/bidList/delete/{id}")
    public HttpStatus deleteBidListById(@PathVariable("id") @Min(1) @Max(127) Integer id, WebRequest request) throws ConstraintViolationException, UnexpectedRollbackException {
        bidListService.deleteBidListById(id); //Throws UnexpectedRollbackException
        log.info("{} : {} : bidList = {} deleted",  requestService.requestToString(request), ((ServletWebRequest) request).getHttpMethod(), id);
        return HttpStatus.OK;
    }   
}