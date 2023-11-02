package com.poseidoninc.poseidon.controller;

import com.poseidoninc.poseidon.domain.BidList;
import com.poseidoninc.poseidon.service.BidListService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BidListControllerTest {
	

	@InjectMocks
	private BidListController bidListController;

	@Mock
	private BidListService bidListService;
	
	@Mock
	private BindingResult bindingResult;
	
	@Mock
	private Model model;

	@Mock
	private WebRequest request;

	@AfterEach
	public void unsetForEachTest() {
		bidListController = null;
	}

	@Test
	@Tag("BidListControllerTest")
	@DisplayName("test home should return \"bidList/list\"")
	public void homeTest() {
		
		//GIVEN
		//WHEN
		String html = bidListController.home(model, request);
		
		//THEN
		assertThat(html).isEqualTo("bidList/list");
	}

	@Test
	@Tag("BidListControllerTest")
	@DisplayName("test addBidForm should return \"bidList/add\"")
	public void bidListAddTestShouldReturnStringBidListAdd() {

		//GIVEN
		BidList bidList = new BidList();

		//WHEN
		String html = bidListController.addBidForm(bidList);

		//THEN
		assertThat(html).isEqualTo("bidList/add");
	}
	@Test
	@Tag("BidListControllerTest")
	@DisplayName("test validate should return \"redirect:/bidList/list\"")
	public void validateTestShouldReturnStringRedirectBidListList() {

		//GIVEN
		BidList bidList = new BidList();
		when(bindingResult.hasErrors()).thenReturn(false);

		//WHEN
		String html = bidListController.validate(bidList, bindingResult, request);

		//THEN
		assertThat(html).isEqualTo("redirect:/bidList/list");
	}

	@Test
	@Tag("BidListControllerTest")
	@DisplayName("test validate should return \"bidList/add\" on BindingResultError")
	public void validateTestShouldReturnStringBidListAddOnBindingResulError() {

		//GIVEN
		BidList bidList = new BidList();
		when(bindingResult.hasErrors()).thenReturn(true);

		//WHEN
		String html = bidListController.validate(bidList, bindingResult, request);

		//THEN
		assertThat(html).isEqualTo("bidList/add");
	}

	@Test
	@Tag("BidListControllerTest")
	@DisplayName("test showUpdateForm should return \"bidList/update\"")
	public void showUpdateFormTestShouldReturnStringBidListUpdate() {

		//GIVEN
		BidList  bidList = new BidList();

		//WHEN
		String html = bidListController.showUpdateForm(1, model, request);

		//THEN
		assertThat(html).isEqualTo("bidList/update");
	}

	@Test
	@Tag("BidListControllerTest")
	@DisplayName("test update Bid should return \"redirect:/bidList/list\"")
	public void updateCurveTestShouldReturnStringRedirectBidListList() {

		//GIVEN
		 BidList bidList = new BidList();
		when(bindingResult.hasErrors()).thenReturn(false);

		//WHEN
		String html = bidListController.updateBid(bidList, bindingResult, request);

		//THEN
		assertThat(html).isEqualTo("redirect:/bidList/list");
	}

	@Test
	@Tag("BidListControllerTest")
	@DisplayName("test update BidList should return \"bidList/update\" on BindingResultError")
	public void updateCurveTestShouldReturnStringBidListUpdateOnBindingResulError() {

		//GIVEN
		BidList bidList = new BidList();
		when(bindingResult.hasErrors()).thenReturn(true);

		//WHEN
		String html = bidListController.updateBid(bidList, bindingResult, request);

		//THEN
		assertThat(html).isEqualTo("bidList/update");
	}

	@Test
	@Tag("BidListControllerTest")
	@DisplayName("test delete BidList should return \"redirect:/bidList/list\"")
	public void deleteCurveTestShouldReturnStringRedirectCurvePointList() {

		//GIVEN
		//WHEN
		String html = bidListController.deleteBid(1, request);

		//THEN
		assertThat(html).isEqualTo("redirect:/bidList/list");
	}
}
