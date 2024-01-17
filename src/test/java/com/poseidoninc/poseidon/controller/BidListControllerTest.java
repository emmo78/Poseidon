package com.poseidoninc.poseidon.controller;

import com.poseidoninc.poseidon.domain.BidList;
import com.poseidoninc.poseidon.service.BidListService;
import com.poseidoninc.poseidon.service.RequestService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
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

	@Mock
	private RequestService requestService;

	@AfterEach
	public void unsetForEachTest() {
		bidListService = null;
		bidListController = null;
	}

	@Nested
	@Tag("homeBidListControllerTests")
	@DisplayName("Tests for /bidList/list")
	class HomeBidListControllerTests {
		@Test
		@Tag("BidListControllerTest")
		@DisplayName("test home should return \"bidList/list\"")
		public void homeTestShouldReturnBidListList() {

			//GIVEN
			when(bidListService.getBidLists(any(Pageable.class))).thenReturn(new PageImpl<BidList>(new ArrayList<>()));

			//WHEN
			String html = bidListController.home(model, request);

			//THEN
			assertThat(html).isEqualTo("bidList/list");
		}

		@Test
		@Tag("BidListControllerTest")
		@DisplayName("test home should throw UnexpectedRollbackException")
		public void homeTestShouldThrowUnexpectedRollbackException() {

			//GIVEN
			when(bidListService.getBidLists(any(Pageable.class))).thenThrow(new UnexpectedRollbackException("Error while getting bidLists"));

			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> bidListController.home(model, request))
					.getMessage()).isEqualTo("Error while getting bidLists");
		}
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

	@Nested
	@Tag("validateBidListControllerTests")
	@DisplayName("Tests for /bidList/validate")
	class ValidateBidListControllerTests {

		@Test
		@Tag("BidListControllerTest")
		@DisplayName("test validate should return \"redirect:/bidList/list\"")
		public void validateTestShouldReturnStringRedirectBidListList() {

			//GIVEN
			BidList bidList = new BidList();
			when(bindingResult.hasErrors()).thenReturn(false);
			when(bidListService.saveBidList(any(BidList.class))).thenReturn(bidList);

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
		@DisplayName("test validate should throw UnexpectedRollbackException")
		public void validateTestShouldThrowUnexpectedRollbackException() {

			//GIVEN
			BidList bidList = new BidList();
			when(bindingResult.hasErrors()).thenReturn(false);
			when(bidListService.saveBidList(any(BidList.class))).thenThrow(new UnexpectedRollbackException("Error while saving bidList"));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> bidListController.validate(bidList, bindingResult, request))
					.getMessage()).isEqualTo("Error while saving bidList");
		}
	}

	@Nested
	@Tag("showUpdateFormBidListControllerTests")
	@DisplayName("Tests for /bidList/update/{id}")
	class ShowUpdateFormBidListControllerTests {

		@Test
		@Tag("BidListControllerTest")
		@DisplayName("test showUpdateForm should return \"bidList/update\"")
		public void showUpdateFormTestShouldReturnStringBidListUpdate() {

			//GIVEN
			BidList  bidList = new BidList();
			when(bidListService.getBidListById(anyInt())).thenReturn(bidList);

			//WHEN
			String html = bidListController.showUpdateForm(1, model, request);

			//THEN
			assertThat(html).isEqualTo("bidList/update");
		}

		@Test
		@Tag("BidListControllerTest")
		@DisplayName("test showUpdateForm should throw UnexpectedRollbackException")
		public void showUpdateFormTestShouldThrowUnexpectedRollbackException() {

			//GIVEN
			when(bidListService.getBidListById(anyInt())).thenThrow(new UnexpectedRollbackException("Error while getting bidList"));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> bidListController.showUpdateForm(1, model, request))
					.getMessage()).isEqualTo("Error while getting bidList");
		}
	}

	@Nested
	@Tag("updateBidBidListControllerTests")
	@DisplayName("Tests for /bidList/update")
	class updateBidBidListControllerTests {

		@Test
		@Tag("BidListControllerTest")
		@DisplayName("test update Bid should return \"redirect:/bidList/list\"")
		public void updateBidTestShouldReturnStringRedirectBidListList() {

			//GIVEN
			BidList bidList = new BidList();
			when(bindingResult.hasErrors()).thenReturn(false);
			when(bidListService.saveBidList(any(BidList.class))).thenReturn(bidList);

			//WHEN
			String html = bidListController.updateBid(bidList, bindingResult, request);

			//THEN
			assertThat(html).isEqualTo("redirect:/bidList/list");
		}

		@Test
		@Tag("BidListControllerTest")
		@DisplayName("test update Bid should return \"bidList/update\" on BindingResultError")
		public void updateBidTestShouldReturnStringBidListUpdateOnBindingResulError() {

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
		@DisplayName("test update Bid should throw UnexpectedRollbackException")
		public void updateBidTestShouldThrowUnexpectedRollbackException() {

			//GIVEN
			BidList bidList = new BidList();
			when(bindingResult.hasErrors()).thenReturn(false);
			when(bidListService.saveBidList(any(BidList.class))).thenThrow(new UnexpectedRollbackException("Error while saving bidList"));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> bidListController.updateBid(bidList, bindingResult, request))
					.getMessage()).isEqualTo("Error while saving bidList");
		}
	}

	@Nested
	@Tag("deleteBidBidListControllerTests")
	@DisplayName("Tests for /bidList/delete/{id}")
	class deleteBidBidListControllerTests {

		@Test
		@Tag("BidListControllerTest")
		@DisplayName("test delete Bid should return \"redirect:/bidList/list\"")
		public void deleteBidTestShouldReturnStringRedirectBidListListList() {

			//GIVEN
			//WHEN
			String html = bidListController.deleteBid(1, request);

			//THEN
			assertThat(html).isEqualTo("redirect:/bidList/list");
		}

		@Test
		@Tag("BidListControllerTest")
		@DisplayName("test delete Bid should throw UnexpectedRollbackException")
		public void deleteBidTestShouldThrowUnexpectedRollbackException() {

			//GIVEN
			doThrow(new UnexpectedRollbackException("Error while deleting bidList")).when(bidListService).deleteBidListById(anyInt());
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> bidListController.deleteBid(1, request))
					.getMessage()).isEqualTo("Error while deleting bidList");
		}
	}
}
