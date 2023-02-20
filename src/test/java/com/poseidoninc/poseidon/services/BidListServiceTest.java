package com.poseidoninc.poseidon.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
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
	@DisplayName("Tests for getting bidList by Id")
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
		@Tag("RegisteredServiceTest")
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
	/*
	@Nested
	@Tag("saveBidListTests")
	@DisplayName("Tests for saving bidLists")
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
			bidList.setId(1);
			bidList.setBidListname("Aaa");
			bidList.setPassword("aaa1=Passwd");
			bidList.setFullname("AAA");
			bidList.setRole("USER");
		}
		
		@AfterEach
		public void unSetForEachTests() {
			bidListService = null;
			bidList = null;
		}

		@ParameterizedTest(name = "id = {0}, bidListToSaveName = {1}, existsByBidListName return {2}, saveBidList should return bidList")
		@CsvSource(value = {"null, Aaa, false",
							"1, Aaa, true",
							"1, Bbb, false"}
							,nullValues = {"null"})
		@Tag("BidListServiceTest")
		@DisplayName("test saveBidList should return bidList")
		public void saveBidListTestShouldReturnBidList(Integer id, String bidListToSaveName, boolean existsByBidListName) {
			
			//GIVEN
			BidList bidListToSave = new BidList();
			bidListToSave.setId(id);
			bidListToSave.setBidListname(bidListToSaveName);
			bidListToSave.setPassword("aaa1=Passwd");
			bidListToSave.setFullname("AAA");
			bidListToSave.setRole("USER");
			
			when(bidListRepository.findById(nullable(Integer.class))).then(invocation -> {
				Integer index = invocation.getArgument(0);
				if (index == null) {
					throw new IllegalArgumentException ("Id must not be null");
				} else {
					return Optional.of(bidList); 
				}
			});
			lenient().when(bidListRepository.existsByBidListname(anyString())).thenReturn(existsByBidListName);
			ArgumentCaptor<BidList> bidListBeingSaved = ArgumentCaptor.forClass(BidList.class);
			when(bidListRepository.save(any(BidList.class))).thenReturn(bidListToSave);
			
			//WHEN
			BidList resultedBidList = bidListService.saveBidList(bidListToSave, request);
			
			//THEN
			verify(bidListRepository, times(1)).save(bidListBeingSaved.capture());
			passwordEncoder.matches("aaa1=Passwd", bidListBeingSaved.getValue().getPassword());
			assertThat(resultedBidList).extracting(
					BidList::getBidListname,
					BidList::getFullname,
					BidList::getRole)
				.containsExactly(
					bidListToSaveName,
					"AAA",
					"USER");	
		}
		
		@ParameterizedTest(name = "id = {0}, bidListToSaveName = {1}, existsByBidListName return {2}, saveBidList should throw ResourceConflictException")
		@CsvSource(value = {"null, Aaa, true",
							"1, Bbb, true"}
							,nullValues = {"null"})
		@Tag("BidListServiceTest")
		@DisplayName("test saveBidList should throw ResourceConflictException")
		public void saveBidListTestShouldThrowsResourceConflictException(Integer id, String bidListToSaveName, boolean existsByBidListName) {

			//GIVEN
			BidList bidListToSave = new BidList();
			bidListToSave.setId(id);
			bidListToSave.setBidListname(bidListToSaveName);
			bidListToSave.setPassword("aaa1=Passwd");
			bidListToSave.setFullname("AAA");
			bidListToSave.setRole("USER");

			when(bidListRepository.findById(nullable(Integer.class))).thenReturn(Optional.of(bidList));
			lenient().when(bidListRepository.existsByBidListname(anyString())).thenReturn(existsByBidListName);

			//WHEN
			//THEN
			assertThat(assertThrows(ResourceConflictException.class,
					() -> bidListService.saveBidList(bidListToSave, request))
					.getMessage()).isEqualTo("BidListName already exists");
		}
		
		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test saveBidList with unknow id should throw a ResourceNotFoundException")
		public void saveBidListTestWithUnknownIdShouldThrowAResourceNotFoundException() {
			
			//GIVEN
			BidList bidListToSave = new BidList();
			bidListToSave.setId(1);
			bidListToSave.setBidListname("Aaa");
			bidListToSave.setPassword("aaa1=Passwd");
			bidListToSave.setFullname("AAA");
			bidListToSave.setRole("USER");

			when(bidListRepository.findById(nullable(Integer.class))).thenThrow(new ResourceNotFoundException("BidList not found"));

			//WHEN
			//THEN
			assertThat(assertThrows(ResourceNotFoundException.class,
					() -> bidListService.saveBidList(bidListToSave, request))
					.getMessage()).isEqualTo("BidList not found");
		}	
		
		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test saveBidList should throw UnexpectedRollbackException on any RuntimeException")
		public void saveBidListTestShouldThrowUnexpectedRollbackExceptionOnAnyRuntimeException() {
			
			//GIVEN
			BidList bidListToSave = new BidList();
			bidListToSave.setId(1);
			bidListToSave.setBidListname("Aaa");
			bidListToSave.setPassword("aaa1=Passwd");
			bidListToSave.setFullname("AAA");
			bidListToSave.setRole("USER");

			when(bidListRepository.findById(nullable(Integer.class))).thenReturn(Optional.of(bidList));
			lenient().when(bidListRepository.existsByBidListname(anyString())).thenReturn(true);
			when(bidListRepository.save(any(BidList.class))).thenThrow(new RuntimeException());

			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> bidListService.saveBidList(bidListToSave, request))
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
			bidList.setId(1);
			bidList.setBidListname("Aaa");
			bidList.setPassword("aaa1=Passwd");
			bidList.setFullname("AAA");
			bidList.setRole("USER");
		}
		
		@AfterEach
		public void unSetForEachTests() {
			bidListService = null;
			bidList = null;
		}

		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test deleteBidList should delete it")
		public void deleteBidListTestShouldDeleteIt() {
			
			//GIVEN
			when(bidListRepository.findById(anyInt())).thenReturn(Optional.of(bidList));
			ArgumentCaptor<BidList> bidListBeingDeleted = ArgumentCaptor.forClass(BidList.class);
			doNothing().when(bidListRepository).delete(any(BidList.class));// Needed to Capture bidList
			
			//WHEN
			bidListService.deleteBidList(1, request);
			
			//THEN
			verify(bidListRepository, times(1)).delete(bidListBeingDeleted.capture());
			assertThat(bidListBeingDeleted.getValue()).extracting(
					BidList::getId,
					BidList::getBidListname,
					BidList::getPassword,
					BidList::getFullname,
					BidList::getRole)
				.containsExactly(
					1,
					"Aaa",
					"aaa1=Passwd",
					"AAA",
					"USER");	
		}
		
		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test deleteBidList should throw ResourceNotFoundException")
		public void saveBidListTestShouldThrowUnexpectedRollbackExceptionOnResourceNotFoundException() {

			//GIVEN
			when(bidListRepository.findById(anyInt())).thenThrow(new ResourceNotFoundException("BidList not found"));

			//WHEN
			//THEN
			assertThat(assertThrows(ResourceNotFoundException.class,
					() -> bidListService.deleteBidList(2, request))
					.getMessage()).isEqualTo("BidList not found");
		}	

		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test deleteBidList should throw UnexpectedRollbackException On Any RuntimeExpceptioin")
		public void deleteBidListTestShouldThrowsUnexpectedRollbackExceptionOnAnyRuntimeException() {

			//GIVEN
			when(bidListRepository.findById(anyInt())).thenReturn(Optional.of(bidList));
			doThrow(new RuntimeException()).when(bidListRepository).delete(any(BidList.class));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> bidListService.deleteBidList(1, request))
					.getMessage()).isEqualTo("Error while deleting bidList");
		}	
	}
*/
}
