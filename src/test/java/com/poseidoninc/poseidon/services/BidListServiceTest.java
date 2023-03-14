package com.poseidoninc.poseidon.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.BidList;
import com.poseidoninc.poseidon.exception.ResourceNotFoundException;
import com.poseidoninc.poseidon.repositories.BidListRepository;

@ExtendWith(MockitoExtension.class)
public class BidListServiceTest {
	@InjectMocks
	private BidListServiceImpl bidListService;
	
	@Mock
	private BidListRepository bidListRepository;
	
	@Spy
	private final RequestService requestService = new RequestServiceImpl();
	
	private MockHttpServletRequest requestMock;
	private WebRequest request;
	private BidList bidList;

	@Nested
	@Tag("getBidListByIdTests")
	@DisplayName("Tests for getting bidList by bidListId")
	@TestInstance(Lifecycle.PER_CLASS)
	class GetBidListByIdTests {
		
		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/bidList/getById/1");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}
		
		@AfterEach
		public void unSetForEachTests() {
			bidListService = null;
			bidList = null;
		}

		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test getBidListById should return expected bidList")
		public void getBidListByIdTestShouldRetrunExcpectedBidList() {
			
			//GIVEN
			bidList = new BidList();
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
			when(bidListRepository.findById(anyInt())).thenReturn(Optional.of(bidList));
			
			//WHEN
			BidList bidListResult = bidListService.getBidListById(1, request);
			
			//THEN
			assertThat(bidListResult).extracting(
					BidList::getBidListId,
					BidList::getAccount,
					BidList::getType,
					BidList::getBidQuantity,
					BidList::getAskQuantity,
					BidList::getBid,
					BidList::getAsk,
					BidList::getBenchmark,
					bid -> bid.getBidListDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
					BidList::getCommentary,
					BidList::getSecurity,
					BidList::getStatus,
					BidList::getTrader,
					BidList::getBook,
					BidList::getCreationName,
					bid -> bid.getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
					BidList::getRevisionName,
					bid -> bid.getRevisionDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
					BidList::getDealName,
					BidList::getDealType,
					BidList::getSourceListId,
					BidList::getSide)
			.containsExactly(
					1,
					"account",
					"type",
					1.0,
					3.0,
					4.0,
					5.0,
					"benchmark",
					"21/01/2023 10:20:30",
					"commentary",
					"security",
					"status",
					"trader",
					"book",
					"creation name",
					"22/01/2023 12:22:32",
					"revisionName",
					"23/01/2023 13:23:33",
					"deal name",
					"deal type",
					"source list id",
					"side"
					);
		}
				
		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test getBidListById should throw IllegalArgumentException")
		public void getBidListByIdTestShouldThrowsUnexpectedRollbackExceptionOnIllegalArgumentException() {
			//GIVEN
			when(bidListRepository.findById(nullable(Integer.class))).thenThrow(new IllegalArgumentException());
			
			//WHEN
			//THEN
			assertThat(assertThrows(IllegalArgumentException.class,
				() -> bidListService.getBidListById(null, request))
				.getMessage()).isEqualTo("Id must not be null");
		}

		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test getBidListById should throw ResourceNotFoundException")
		public void getBidListByIdTestShouldThrowsResourceNotFoundException() {
			//GIVEN
			when(bidListRepository.findById(anyInt())).thenReturn(Optional.ofNullable(null));
			
			//WHEN
			//THEN
			assertThat(assertThrows(ResourceNotFoundException.class,
				() -> bidListService.getBidListById(1, request))
				.getMessage()).isEqualTo("BidList not found");
		}
		
		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test getBidListById should throw UnexpectedRollbackException on any RuntimeException")
		public void getBidListByIdTestShouldThrowsUnexpectedRollbackExceptionOnAnyRuntimeException() {
			//GIVEN
			when(bidListRepository.findById(anyInt())).thenThrow(new RuntimeException());
			
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
				() -> bidListService.getBidListById(1, request))
				.getMessage()).isEqualTo("Error while getting bidlist");
		}
	}
	
	@Nested
	@Tag("getBidListsTests")
	@DisplayName("Tests for getting bidLists")
	@TestInstance(Lifecycle.PER_CLASS)
	class GetBidListsTests {
		
		private Pageable pageRequest;
		
		@BeforeAll
		public void setUpForAllTests() {
			pageRequest = Pageable.unpaged();
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/bidList/getBidLists");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			pageRequest = null;
			requestMock = null;
			request = null;
		}
		
		@AfterEach
		public void unSetForEachTests() {
			bidListService = null;
			bidList = null;
		}

		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test getBidLists should return bidLists")
		public void getBidListsTesthouldRetrunExcpectedBidLists() {
			
			//GIVEN
			List<BidList> expectedBidLists = new ArrayList<>();
			bidList = new BidList();
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
			when(bidListRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<BidList>(expectedBidLists, pageRequest, 2));
			
			//WHEN
			Page<BidList> resultedBidLists = bidListService.getBidLists(pageRequest, request);
			
			//THEN
			assertThat(resultedBidLists).containsExactlyElementsOf(expectedBidLists);
		}
		
		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test getBidLists should throw UnexpectedRollbackException on NullPointerException")
		public void getBidListsTestShouldThrowsUnexpectedRollbackExceptionOnNullPointerException() {
			//GIVEN
			when(bidListRepository.findAll(nullable(Pageable.class))).thenThrow(new NullPointerException());
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> bidListService.getBidLists(pageRequest, request))
					.getMessage()).isEqualTo("Error while getting BidLists");
		}
		
		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test getBidLists should throw UnexpectedRollbackException on any RuntimeException")
		public void getBidListsTestShouldThrowsUnexpectedRollbackExceptionOnAnyRuntimeException() {
			//GIVEN
			when(bidListRepository.findAll(any(Pageable.class))).thenThrow(new RuntimeException());
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> bidListService.getBidLists(pageRequest, request))
					.getMessage()).isEqualTo("Error while getting BidLists");
		}
	}
	
	@Nested
	@Tag("saveBidListTests")
	@DisplayName("Tests for saving bidList")
	@TestInstance(Lifecycle.PER_CLASS)
	class SaveBidListTests {
		
		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/bidList/saveBidList/");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}
		
		@BeforeEach
		public void setUpForEachTest() {
			bidList = new BidList();
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
		}
		
		@AfterEach
		public void unSetForEachTests() {
			bidListService = null;
			bidList = null;
		}

		@ParameterizedTest(name = "{0} bidList to save, so id = {1}, saveBidList should return bidList with an id")
		@CsvSource(value = {"new, null", // save new bidList
							"updated, 1"} // save updated bidList
							,nullValues = {"null"})
		@Tag("BidListServiceTest")
		@DisplayName("test saveBidList should return bidList")
		public void saveBidListTestShouldReturnBidList(String state, Integer id) {
			
			//GIVEN
			bidList.setBidListId(id);
			when(bidListRepository.save(any(BidList.class))).then(invocation -> {
				BidList bidListSaved = invocation.getArgument(0);
				bidListSaved.setBidListId(Optional.ofNullable(bidListSaved.getBidListId()).orElseGet(() -> 1));
				return bidListSaved;
				});
			
			//WHEN
			BidList bidListResult = bidListService.saveBidList(bidList, request);
			
			//THEN
			verify(bidListRepository).save(bidList); //times(1) is the default and can be omitted
			assertThat(bidListResult).extracting(
					BidList::getBidListId,
					BidList::getAccount,
					BidList::getType,
					BidList::getBidQuantity,
					BidList::getAskQuantity,
					BidList::getBid,
					BidList::getAsk,
					BidList::getBenchmark,
					bid -> bid.getBidListDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
					BidList::getCommentary,
					BidList::getSecurity,
					BidList::getStatus,
					BidList::getTrader,
					BidList::getBook,
					BidList::getCreationName,
					bid -> bid.getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
					BidList::getRevisionName,
					bid -> bid.getRevisionDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
					BidList::getDealName,
					BidList::getDealType,
					BidList::getSourceListId,
					BidList::getSide)
			.containsExactly(
					1,
					"account",
					"type",
					1.0,
					3.0,
					4.0,
					5.0,
					"benchmark",
					"21/01/2023 10:20:30",
					"commentary",
					"security",
					"status",
					"trader",
					"book",
					"creation name",
					"22/01/2023 12:22:32",
					"revisionName",
					"23/01/2023 13:23:33",
					"deal name",
					"deal type",
					"source list id",
					"side"
					);
		}
				
		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test saveBidList should throw UnexpectedRollbackException on any RuntimeException")
		public void saveBidListTestShouldThrowUnexpectedRollbackExceptionOnAnyRuntimeException() {
			
			//GIVEN
			bidList.setBidListId(1);
			when(bidListRepository.save(any(BidList.class))).thenThrow(new RuntimeException());

			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> bidListService.saveBidList(bidList, request))
					.getMessage()).isEqualTo("Error while saving bidList");
		}	
	}

	@Nested
	@Tag("deleteBidListTests")
	@DisplayName("Tests for deleting bidLists")
	@TestInstance(Lifecycle.PER_CLASS)
	class DeleteBidListTests {
		
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
		
		@BeforeEach
		public void setUpForEachTest() {
			bidList = new BidList();
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
		}
		
		@AfterEach
		public void unSetForEachTests() {
			bidListService = null;
			bidList = null;
		}

		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test deleteBidList by Id should delete it")
		public void deleteBidListByIdTestShouldDeleteIt() {
			
			//GIVEN
			when(bidListRepository.findById(anyInt())).thenReturn(Optional.of(bidList));
			ArgumentCaptor<BidList> bidListBeingDeleted = ArgumentCaptor.forClass(BidList.class);
			doNothing().when(bidListRepository).delete(any(BidList.class));// Needed to Capture bidList
			
			//WHEN
			bidListService.deleteBidListById(1, request);
			
			//THEN
			verify(bidListRepository, times(1)).delete(bidListBeingDeleted.capture());
			assertThat(bidListBeingDeleted.getValue()).extracting(
					BidList::getBidListId,
					BidList::getAccount,
					BidList::getType,
					BidList::getBidQuantity,
					BidList::getAskQuantity,
					BidList::getBid,
					BidList::getAsk,
					BidList::getBenchmark,
					bid -> bid.getBidListDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
					BidList::getCommentary,
					BidList::getSecurity,
					BidList::getStatus,
					BidList::getTrader,
					BidList::getBook,
					BidList::getCreationName,
					bid -> bid.getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
					BidList::getRevisionName,
					bid -> bid.getRevisionDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
					BidList::getDealName,
					BidList::getDealType,
					BidList::getSourceListId,
					BidList::getSide)
			.containsExactly(
					1,
					"account",
					"type",
					1.0,
					3.0,
					4.0,
					5.0,
					"benchmark",
					"21/01/2023 10:20:30",
					"commentary",
					"security",
					"status",
					"trader",
					"book",
					"creation name",
					"22/01/2023 12:22:32",
					"revisionName",
					"23/01/2023 13:23:33",
					"deal name",
					"deal type",
					"source list id",
					"side"
					);
		}
		
		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test deleteBidList by Id by Id should throw ResourceNotFoundException")
		public void deleteBidListByIdTestShouldThrowUnexpectedRollbackExceptionOnResourceNotFoundException() {

			//GIVEN
			when(bidListRepository.findById(anyInt())).thenThrow(new ResourceNotFoundException("BidList not found"));

			//WHEN
			//THEN
			assertThat(assertThrows(ResourceNotFoundException.class,
					() -> bidListService.deleteBidListById(2, request))
					.getMessage()).isEqualTo("BidList not found");
		}	

		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test deleteBidList by Id should throw UnexpectedRollbackException On IllegalArgumentException()")
		public void deleteBidListByIdTestShouldThrowsUnexpectedRollbackExceptionOnIllegalArgumentException() {

			//GIVEN
			when(bidListRepository.findById(anyInt())).thenReturn(Optional.of(bidList));
			doThrow(new IllegalArgumentException()).when(bidListRepository).delete(any(BidList.class));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> bidListService.deleteBidListById(1, request))
					.getMessage()).isEqualTo("Error while deleting bidList");
		}	

		
		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test deleteBidList by Id should throw UnexpectedRollbackException On Any RuntimeExpceptioin")
		public void deleteBidListByIdTestShouldThrowsUnexpectedRollbackExceptionOnAnyRuntimeException() {

			//GIVEN
			when(bidListRepository.findById(anyInt())).thenReturn(Optional.of(bidList));
			doThrow(new RuntimeException()).when(bidListRepository).delete(any(BidList.class));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> bidListService.deleteBidListById(1, request))
					.getMessage()).isEqualTo("Error while deleting bidList");
		}	
	}
}
