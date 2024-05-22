package com.poseidoninc.poseidon.controller;

import com.poseidoninc.poseidon.domain.BidList;
import com.poseidoninc.poseidon.service.BidListService;
import com.poseidoninc.poseidon.service.RequestService;
import com.poseidoninc.poseidon.service.RequestServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * unit test class for the BidListController.
 * @author olivier morel
 */
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

	@Spy
	private final RequestService requestService = new RequestServiceImpl();

	private MockHttpServletRequest requestMock;
	private WebRequest request;

	@AfterEach
	public void unsetForEachTest() {
		bidListService = null;
		bidListController = null;
	}

	@Nested
	@Tag("homeBidListControllerTests")
	@DisplayName("Tests for /bidList/list")
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class HomeBidListControllerTests {

		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/bidList/list");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}

		@Test
		@Tag("BidListControllerTest")
		@DisplayName("test home should return \"bidList/list\"")
		public void homeTestShouldReturnStringBidListList() {

			//GIVEN
			List<BidList> expectedBidLists = new ArrayList<>();
			BidList bidList = new BidList();
			bidList.setBidListId(1);
			bidList.setAccount("account");
			bidList.setType("type");
			bidList.setBidQuantity(1.0);
			bidList.setAskQuantity(3.0);
			bidList.setBid(4.0);
			bidList.setAsk(5.0);
			bidList.setBenchmark("benchmark");
			bidList.setBidListDate(LocalDateTime.parse("21/01/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			bidList.setCommentary("commentary");
			bidList.setSecurity("security");
			bidList.setStatus("status");
			bidList.setTrader("trader");
			bidList.setBook("book");
			bidList.setCreationName("creation name");
			bidList.setCreationDate(LocalDateTime.parse("22/01/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			bidList.setRevisionName("revisionName");
			bidList.setRevisionDate(LocalDateTime.parse("23/01/2023 13:23:33", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			bidList.setDealName("deal name");
			bidList.setDealType("deal type");
			bidList.setSourceListId("source list id");
			bidList.setSide("side");
			expectedBidLists.add(bidList);

			BidList bidList2 = new BidList();
			bidList2.setBidListId(12);
			bidList2.setAccount("account2");
			bidList2.setType("type2");
			bidList2.setBidQuantity(1.02);
			bidList2.setAskQuantity(3.02);
			bidList2.setBid(4.02);
			bidList2.setAsk(5.02);
			bidList2.setBenchmark("benchmark2");
			bidList2.setBidListDate(LocalDateTime.parse("11/01/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			bidList2.setCommentary("commentary2");
			bidList2.setSecurity("security2");
			bidList2.setStatus("status2");
			bidList2.setTrader("trader2");
			bidList2.setBook("book2");
			bidList2.setCreationName("creation name2");
			bidList2.setCreationDate(LocalDateTime.parse("12/01/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			bidList2.setRevisionName("revisionName2");
			bidList2.setRevisionDate(LocalDateTime.parse("12/01/2023 13:23:33", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			bidList2.setDealName("deal name2");
			bidList2.setDealType("deal type2");
			bidList2.setSourceListId("source list id2");
			bidList2.setSide("side2");
			expectedBidLists.add(bidList2);

			ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
			ArgumentCaptor<Iterable<BidList>> iterableArgumentCaptor = ArgumentCaptor.forClass(Iterable.class);
			when(bidListService.getBidLists(any(Pageable.class))).thenReturn(new PageImpl<BidList>(expectedBidLists));

			//WHEN
			String html = bidListController.home(model, request);

			//THEN
			verify(model).addAttribute(stringArgumentCaptor.capture(), iterableArgumentCaptor.capture()); //times(1) is used by default
			assertThat(stringArgumentCaptor.getValue()).isEqualTo("bidLists");
			assertThat(iterableArgumentCaptor.getValue()).containsExactlyElementsOf(expectedBidLists);
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
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class ValidateBidListControllerTests {

		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/bidList/validate/");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}

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
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class ShowUpdateFormBidListControllerTests {

		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/bidList/update/1");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}

		@Test
		@Tag("BidListControllerTest")
		@DisplayName("test showUpdateForm should return \"bidList/update\"")
		public void showUpdateFormTestShouldReturnStringBidListUpdate() {

			//GIVEN
			BidList bidList = new BidList();
			bidList.setBidListId(1);
			bidList.setAccount("account");
			bidList.setType("type");
			bidList.setBidQuantity(1.0);
			bidList.setAskQuantity(3.0);
			bidList.setBid(4.0);
			bidList.setAsk(5.0);
			bidList.setBenchmark("benchmark");
			bidList.setBidListDate(LocalDateTime.parse("21/01/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			bidList.setCommentary("commentary");
			bidList.setSecurity("security");
			bidList.setStatus("status");
			bidList.setTrader("trader");
			bidList.setBook("book");
			bidList.setCreationName("creation name");
			bidList.setCreationDate(LocalDateTime.parse("22/01/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			bidList.setRevisionName("revisionName");
			bidList.setRevisionDate(LocalDateTime.parse("23/01/2023 13:23:33", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			bidList.setDealName("deal name");
			bidList.setDealType("deal type");
			bidList.setSourceListId("source list id");
			bidList.setSide("side");
			ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
			ArgumentCaptor<BidList> bidListArgumentCaptor = ArgumentCaptor.forClass(BidList.class);
			when(bidListService.getBidListById(anyInt())).thenReturn(bidList);

			//WHEN
			String html = bidListController.showUpdateForm(1, model, request);

			//THEN
			verify(model).addAttribute(stringArgumentCaptor.capture(), bidListArgumentCaptor.capture()); //times(1) is used by default
			assertThat(stringArgumentCaptor.getValue()).isEqualTo("bidList");
			assertThat(bidListArgumentCaptor.getValue()).isEqualTo(bidList);
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
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class updateBidBidListControllerTests {

		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/bidList/update/");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}

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
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class deleteBidBidListControllerTests {

		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/bidList/delete/1");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}

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
