package com.poseidoninc.poseidon.service;

import com.poseidoninc.poseidon.domain.BidList;
import com.poseidoninc.poseidon.repository.BidListRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.UnexpectedRollbackException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * unit test class for the BidListService.
 * @author olivier morel
 */
@ExtendWith(MockitoExtension.class)
public class BidListServiceTest {
	@InjectMocks
	private BidListServiceImpl bidListService;
	
	@Mock
	private BidListRepository bidListRepository;
	
	private BidList bidList;

	@AfterEach
	public void unSetForEachTests() {
		bidListService = null;
		bidList = null;
	}

	@Nested
	@Tag("getBidListByIdTests")
	@DisplayName("Tests for getting bidList by bidListId")
	class GetBidListByIdTests {
		
		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test getBidListById should return expected bidList")
		public void getBidListByIdTestShouldReturnExpectedBidList() {
			
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
			BidList bidListResult = bidListService.getBidListById(1);
			
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
		@DisplayName("test getBidListById should throw UnexpectedRollbackException on InvalidDataAccessApiUsageException")
		public void getBidListByIdTestShouldThrowsUnexpectedRollbackExceptionOnInvalidDataAccessApiUsageException() {
			//GIVEN
			when(bidListRepository.findById(nullable(Integer.class))).thenThrow(new InvalidDataAccessApiUsageException("The given id must not be null"));
			
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
				() -> bidListService.getBidListById(null))
				.getMessage()).isEqualTo("Error while getting bidlist");
		}

		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test getBidListById should throw UnexpectedRollbackException on ResourceNotFoundException")
		public void getBidListByIdTestShouldThrowsUnexpectedRollbackExceptionOnResourceNotFoundException() {
			//GIVEN
			when(bidListRepository.findById(anyInt())).thenReturn(Optional.empty());
			
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
				() -> bidListService.getBidListById(1))
				.getMessage()).isEqualTo("Error while getting bidlist");
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
				() -> bidListService.getBidListById(1))
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
		}

		@AfterAll
		public void unSetForAllTests() {
			pageRequest = null;
		}

		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test getBidLists should return bidLists")
		public void getBidListsTestShouldReturnBidLists() {

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
			Page<BidList> resultedBidLists = bidListService.getBidLists(pageRequest);

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
					() -> bidListService.getBidLists(pageRequest))
					.getMessage()).isEqualTo("Error while getting bidLists");
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
					() -> bidListService.getBidLists(pageRequest))
					.getMessage()).isEqualTo("Error while getting bidLists");
		}
	}

	@Nested
	@Tag("saveBidListTests")
	@DisplayName("Tests for saving bidList")
	class SaveBidListTests {

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

		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test saveBidList should persist and return bidList")
		public void saveBidListTestShouldReturnBidList() {

			//GIVEN
			BidList bidListExpected = new BidList();
			bidListExpected.setBidListId(1);
			bidListExpected.setAccount("account");
			bidListExpected.setType("type");
			bidListExpected.setBidQuantity(1.0);
			bidListExpected.setAskQuantity(3.0);
			bidListExpected.setBid(4.0);
			bidListExpected.setAsk(5.0);
			bidListExpected.setBenchmark("benchmark");
			bidListExpected.setBidListDate(LocalDateTime.parse("21/01/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			bidListExpected.setCommentary("commentary");
			bidListExpected.setSecurity("security");
			bidListExpected.setStatus("status");
			bidListExpected.setTrader("trader");
			bidListExpected.setBook("book");
			bidListExpected.setCreationName("creation name");
			bidListExpected.setCreationDate(LocalDateTime.parse("22/01/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			bidListExpected.setRevisionName("revisionName");
			bidListExpected.setRevisionDate(LocalDateTime.parse("23/01/2023 13:23:33", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			bidListExpected.setDealName("deal name");
			bidListExpected.setDealType("deal type");
			bidListExpected.setSourceListId("source list id");
			bidListExpected.setSide("side");

			when(bidListRepository.save(any(BidList.class))).thenReturn(bidListExpected);

			//WHEN
			BidList bidListResult = bidListService.saveBidList(bidList);

			//THEN
			verify(bidListRepository).save(any(BidList.class)); //times(1) is the default and can be omitted
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
			when(bidListRepository.save(any(BidList.class))).thenThrow(new RuntimeException());

			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> bidListService.saveBidList(bidList))
					.getMessage()).isEqualTo("Error while saving bidList");
		}
	}

	@Nested
	@Tag("deleteBidListTests")
	@DisplayName("Tests for deleting bidLists")
	class DeleteBidListTests {

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

		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test deleteBidList by Id should delete it")
		public void deleteBidListByIdTestShouldDeleteIt() {

			//GIVEN
			when(bidListRepository.findById(anyInt())).thenReturn(Optional.of(bidList));
			ArgumentCaptor<BidList> bidListBeingDeleted = ArgumentCaptor.forClass(BidList.class);
			doNothing().when(bidListRepository).delete(any(BidList.class));// Needed to Capture bidList

			//WHEN
			bidListService.deleteBidListById(1);

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
		@DisplayName("test deleteBidList by Id by Id should throw UnexpectedRollbackException on UnexpectedRollbackException")
		public void deleteBidListByIdTestShouldThrowUnexpectedRollbackExceptionOnUnexpectedRollbackException() {

			//GIVEN
			when(bidListRepository.findById(anyInt())).thenThrow(new UnexpectedRollbackException("Error while getting bidlist"));

			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> bidListService.deleteBidListById(2))
					.getMessage()).isEqualTo("Error while deleting bidList");
		}

		@Test
		@Tag("BidListServiceTest")
		@DisplayName("test deleteBidList by Id should throw UnexpectedRollbackException On InvalidDataAccessApiUsageException()")
		public void deleteBidListByIdTestShouldThrowsUnexpectedRollbackExceptionOnInvalidDataAccessApiUsageException() {

			//GIVEN
			when(bidListRepository.findById(anyInt())).thenReturn(Optional.of(bidList));
			doThrow(new InvalidDataAccessApiUsageException("Entity must not be null")).when(bidListRepository).delete(any(BidList.class));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> bidListService.deleteBidListById(1))
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
					() -> bidListService.deleteBidListById(1))
					.getMessage()).isEqualTo("Error while deleting bidList");
		}	
	}
}
