package com.poseidoninc.poseidon.controller;

import com.poseidoninc.poseidon.domain.Trade;
import com.poseidoninc.poseidon.service.TradeService;
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
public class TradeControllerTest {
	

	@InjectMocks
	private TradeController tradeController;

	@Mock
	private TradeService tradeService;
	
	@Mock
	private BindingResult bindingResult;
	
	@Mock
	private Model model;

	@Mock
	private WebRequest request;

	@AfterEach
	public void unsetForEachTest() {
		tradeController = null;
	}

	@Test
	@Tag("TradeControllerTest")
	@DisplayName("test home should return \"trade/list\"")
	public void homeTest() {
		
		//GIVEN
		//WHEN
		String html = tradeController.home(model, request);
		
		//THEN
		assertThat(html).isEqualTo("trade/list");
	}

	@Test
	@Tag("TradeControllerTest")
	@DisplayName("test addBidForm should return \"trade/add\"")
	public void tradeAddTestShouldReturnStringTradeAdd() {

		//GIVEN
		Trade trade = new Trade();

		//WHEN
		String html = tradeController.addBidForm(trade);

		//THEN
		assertThat(html).isEqualTo("trade/add");
	}
	@Test
	@Tag("TradeControllerTest")
	@DisplayName("test validate should return \"redirect:/trade/list\"")
	public void validateTestShouldReturnStringRedirectTradeList() {

		//GIVEN
		Trade trade = new Trade();
		when(bindingResult.hasErrors()).thenReturn(false);

		//WHEN
		String html = tradeController.validate(trade, bindingResult, request);

		//THEN
		assertThat(html).isEqualTo("redirect:/trade/list");
	}

	@Test
	@Tag("TradeControllerTest")
	@DisplayName("test validate should return \"trade/add\" on BindingResultError")
	public void validateTestShouldReturnStringTradeAddOnBindingResulError() {

		//GIVEN
		Trade trade = new Trade();
		when(bindingResult.hasErrors()).thenReturn(true);

		//WHEN
		String html = tradeController.validate(trade, bindingResult, request);

		//THEN
		assertThat(html).isEqualTo("trade/add");
	}

	@Test
	@Tag("TradeControllerTest")
	@DisplayName("test showUpdateForm should return \"trade/update\"")
	public void showUpdateFormTestShouldReturnStringTradeUpdate() {

		//GIVEN
		Trade  trade = new Trade();

		//WHEN
		String html = tradeController.showUpdateForm(1, model, request);

		//THEN
		assertThat(html).isEqualTo("trade/update");
	}

	@Test
	@Tag("TradeControllerTest")
	@DisplayName("test update Bid should return \"redirect:/trade/list\"")
	public void updateCurveTestShouldReturnStringRedirectTradeList() {

		//GIVEN
		 Trade trade = new Trade();
		when(bindingResult.hasErrors()).thenReturn(false);

		//WHEN
		String html = tradeController.updateBid(trade, bindingResult, request);

		//THEN
		assertThat(html).isEqualTo("redirect:/trade/list");
	}

	@Test
	@Tag("TradeControllerTest")
	@DisplayName("test update Trade should return \"trade/update\" on BindingResultError")
	public void updateCurveTestShouldReturnStringTradeUpdateOnBindingResulError() {

		//GIVEN
		Trade trade = new Trade();
		when(bindingResult.hasErrors()).thenReturn(true);

		//WHEN
		String html = tradeController.updateBid(trade, bindingResult, request);

		//THEN
		assertThat(html).isEqualTo("trade/update");
	}

	@Test
	@Tag("TradeControllerTest")
	@DisplayName("test delete Trade should return \"redirect:/trade/list\"")
	public void deleteCurveTestShouldReturnStringRedirectCurvePointList() {

		//GIVEN
		//WHEN
		String html = tradeController.deleteBid(1, request);

		//THEN
		assertThat(html).isEqualTo("redirect:/trade/list");
	}
}
