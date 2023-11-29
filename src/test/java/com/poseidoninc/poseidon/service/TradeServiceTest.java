package com.poseidoninc.poseidon.service;

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
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.Trade;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import com.poseidoninc.poseidon.repository.TradeRepository;

@ExtendWith(MockitoExtension.class)
public class TradeServiceTest {
	@InjectMocks
	private TradeServiceImpl tradeService;
	
	@Mock
	private TradeRepository tradeRepository;
	
	@Spy
	private final RequestService requestService = new RequestServiceImpl();
	
	private MockHttpServletRequest requestMock;
	private WebRequest request;
	private Trade trade;

	@Nested
	@Tag("getTradeByIdTests")
	@DisplayName("Tests for getting trade by tradeId")
	@TestInstance(Lifecycle.PER_CLASS)
	class GetTradeByIdTests {
		
		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/trade/getById/1");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}
		
		@AfterEach
		public void unSetForEachTests() {
			tradeService = null;
			trade = null;
		}

		@Test
		@Tag("TradeServiceTest")
		@DisplayName("test getTradeById should return expected trade")
		public void getTradeByIdTestShouldReturnExpectedTrade() {
			
			//GIVEN
			trade = new Trade();
			trade.setTradeId(1);
			trade.setAccount("account");
			trade.setType("type");
			trade.setBuyQuantity(3.0);
			trade.setSellQuantity(1.0);
			trade.setBuyPrice(4.0);
			trade.setSellPrice(5.0);
			trade.setTradeDate(LocalDateTime.parse("21/01/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			trade.setSecurity("security");
			trade.setStatus("status");
			trade.setTrader("trader");
			trade.setBenchmark("benchmark");
			trade.setBook("book");
			trade.setCreationName("creation name");
			trade.setCreationDate(LocalDateTime.parse("22/01/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			trade.setRevisionName("revisionName");
			trade.setRevisionDate(LocalDateTime.parse("23/01/2023 13:23:33", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			trade.setDealName("deal name");
			trade.setDealType("deal type");
			trade.setSourceListId("source list id");
			trade.setSide("side");					
			when(tradeRepository.findById(anyInt())).thenReturn(Optional.of(trade));
			
			//WHEN
			Trade tradeResult = tradeService.getTradeById(1, request);
			
			//THEN
			assertThat(tradeResult).extracting(
					Trade::getTradeId,
					Trade::getAccount,
					Trade::getType,
					Trade::getBuyQuantity,
					Trade::getSellQuantity,
					Trade::getBuyPrice,
					Trade::getSellPrice,
					tr -> tr.getTradeDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
					Trade::getSecurity,
					Trade::getStatus,
					Trade::getTrader,
					Trade::getBenchmark,
					Trade::getBook,
					Trade::getCreationName,
					tr -> tr.getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
					Trade::getRevisionName,
					tr -> tr.getRevisionDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
					Trade::getDealName,
					Trade::getDealType,
					Trade::getSourceListId,
					Trade::getSide)
			.containsExactly(
					1,
					"account",
					"type",
					3.0,
					1.0,
					4.0,
					5.0,
					"21/01/2023 10:20:30",
					"security",
					"status",
					"trader",
					"benchmark",
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
		@Tag("TradeServiceTest")
		@DisplayName("test getTradeById should throw UnexpectedRollbackException on InvalidDataAccessApiUsageException")
		public void getTradeByIdTestShouldThrowsUnexpectedRollbackExceptionOnInvalidDataAccessApiUsageException() {
			//GIVEN
			when(tradeRepository.findById(nullable(Integer.class))).thenThrow(new InvalidDataAccessApiUsageException("The given id must not be null"));
			
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
				() -> tradeService.getTradeById(null, request))
				.getMessage()).isEqualTo("Error while getting trade");
		}

		@Test
		@Tag("TradeServiceTest")
		@DisplayName("test getTradeById should throw UnexpectedRollbackException on ResourceNotFoundException")
		public void getTradeByIdTestShouldThrowsUnexpectedRollbackExceptionOnResourceNotFoundException() {
			//GIVEN
			when(tradeRepository.findById(anyInt())).thenReturn(Optional.empty());
			
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
				() -> tradeService.getTradeById(1, request))
				.getMessage()).isEqualTo("Error while getting trade");
		}
		
		@Test
		@Tag("TradeServiceTest")
		@DisplayName("test getTradeById should throw UnexpectedRollbackException on any RuntimeException")
		public void getTradeByIdTestShouldThrowsUnexpectedRollbackExceptionOnAnyRuntimeException() {
			//GIVEN
			when(tradeRepository.findById(anyInt())).thenThrow(new RuntimeException());
			
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
				() -> tradeService.getTradeById(1, request))
				.getMessage()).isEqualTo("Error while getting trade");
		}
	}
	
	@Nested
	@Tag("getTradesTests")
	@DisplayName("Tests for getting trades")
	@TestInstance(Lifecycle.PER_CLASS)
	class GetTradesTests {
		
		private Pageable pageRequest;
		
		@BeforeAll
		public void setUpForAllTests() {
			pageRequest = Pageable.unpaged();
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/trade/getTrades");
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
			tradeService = null;
			trade = null;
		}

		@Test
		@Tag("TradeServiceTest")
		@DisplayName("test getTrades should return trades")
		public void getTradesTestShouldReturnTrades() {
			
			//GIVEN
			List<Trade> expectedTrades = new ArrayList<>();
			trade = new Trade();
			trade.setTradeId(1);
			trade.setAccount("account");
			trade.setType("type");
			trade.setBuyQuantity(3.0);
			trade.setSellQuantity(1.0);
			trade.setBuyPrice(4.0);
			trade.setSellPrice(5.0);
			trade.setTradeDate(LocalDateTime.parse("21/01/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			trade.setSecurity("security");
			trade.setStatus("status");
			trade.setTrader("trader");
			trade.setBenchmark("benchmark");
			trade.setBook("book");
			trade.setCreationName("creation name");
			trade.setCreationDate(LocalDateTime.parse("22/01/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			trade.setRevisionName("revisionName");
			trade.setRevisionDate(LocalDateTime.parse("23/01/2023 13:23:33", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			trade.setDealName("deal name");
			trade.setDealType("deal type");
			trade.setSourceListId("source list id");
			trade.setSide("side");					
			expectedTrades.add(trade);
			
			Trade trade2 = new Trade();
			trade2 = new Trade();
			trade2.setTradeId(2);
			trade2.setAccount("account2");
			trade2.setType("type2");
			trade2.setBuyQuantity(3.2);
			trade2.setSellQuantity(1.2);
			trade2.setBuyPrice(4.2);
			trade2.setSellPrice(5.2);
			trade2.setTradeDate(LocalDateTime.parse("21/02/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			trade2.setSecurity("security2");
			trade2.setStatus("status2");
			trade2.setTrader("trader2");
			trade2.setBenchmark("benchmark2");
			trade2.setBook("book2");
			trade2.setCreationName("creation name2");
			trade2.setCreationDate(LocalDateTime.parse("22/02/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			trade2.setRevisionName("revisionName2");
			trade2.setRevisionDate(LocalDateTime.parse("23/02/2023 13:23:33", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			trade2.setDealName("deal name2");
			trade2.setDealType("deal type2");
			trade2.setSourceListId("source list id2");
			trade2.setSide("side2");					
			expectedTrades.add(trade2);
			when(tradeRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<Trade>(expectedTrades, pageRequest, 2));
			
			//WHEN
			Page<Trade> resultedTrades = tradeService.getTrades(pageRequest, request);
			
			//THEN
			assertThat(resultedTrades).containsExactlyElementsOf(expectedTrades);
		}
		
		@Test
		@Tag("TradeServiceTest")
		@DisplayName("test getTrades should throw UnexpectedRollbackException on NullPointerException")
		public void getTradesTestShouldThrowsUnexpectedRollbackExceptionOnNullPointerException() {
			//GIVEN
			when(tradeRepository.findAll(nullable(Pageable.class))).thenThrow(new NullPointerException());
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> tradeService.getTrades(pageRequest, request))
					.getMessage()).isEqualTo("Error while getting trades");
		}
		
		@Test
		@Tag("TradeServiceTest")
		@DisplayName("test getTrades should throw UnexpectedRollbackException on any RuntimeException")
		public void getTradesTestShouldThrowsUnexpectedRollbackExceptionOnAnyRuntimeException() {
			//GIVEN
			when(tradeRepository.findAll(any(Pageable.class))).thenThrow(new RuntimeException());
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> tradeService.getTrades(pageRequest, request))
					.getMessage()).isEqualTo("Error while getting trades");
		}
	}
	
	@Nested
	@Tag("saveTradeTests")
	@DisplayName("Tests for saving trade")
	@TestInstance(Lifecycle.PER_CLASS)
	class SaveTradeTests {
		
		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/trade/saveTrade/");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}
		
		@BeforeEach
		public void setUpForEachTest() {
			trade = new Trade();
			trade.setAccount("account");
			trade.setType("type");
			trade.setBuyQuantity(3.0);
			trade.setSellQuantity(1.0);
			trade.setBuyPrice(4.0);
			trade.setSellPrice(5.0);
			trade.setTradeDate(LocalDateTime.parse("21/01/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			trade.setSecurity("security");
			trade.setStatus("status");
			trade.setTrader("trader");
			trade.setBenchmark("benchmark");
			trade.setBook("book");
			trade.setCreationName("creation name");
			trade.setCreationDate(LocalDateTime.parse("22/01/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			trade.setRevisionName("revisionName");
			trade.setRevisionDate(LocalDateTime.parse("23/01/2023 13:23:33", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			trade.setDealName("deal name");
			trade.setDealType("deal type");
			trade.setSourceListId("source list id");
			trade.setSide("side");					
		}
		
		@AfterEach
		public void unSetForEachTests() {
			tradeService = null;
			trade = null;
		}

		@Test
		@Tag("TradeServiceTest")
		@DisplayName("test saveTrade should persist and return trade")
		public void saveTradeTestShouldReturnTrade() {
			
			//GIVEN
			when(tradeRepository.save(any(Trade.class))).then(invocation -> {
				Trade tradeSaved = invocation.getArgument(0);
				tradeSaved.setTradeId(1);
				return tradeSaved;
				});
			
			//WHEN
			Trade tradeResult = tradeService.saveTrade(trade, request);
			
			//THEN
			verify(tradeRepository).save(trade); //times(1) is the default and can be omitted
			assertThat(tradeResult).extracting(
					Trade::getTradeId,
					Trade::getAccount,
					Trade::getType,
					Trade::getBuyQuantity,
					Trade::getSellQuantity,
					Trade::getBuyPrice,
					Trade::getSellPrice,
					tr -> tr.getTradeDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
					Trade::getSecurity,
					Trade::getStatus,
					Trade::getTrader,
					Trade::getBenchmark,
					Trade::getBook,
					Trade::getCreationName,
					tr -> tr.getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
					Trade::getRevisionName,
					tr -> tr.getRevisionDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
					Trade::getDealName,
					Trade::getDealType,
					Trade::getSourceListId,
					Trade::getSide)
			.containsExactly(
					1,
					"account",
					"type",
					3.0,
					1.0,
					4.0,
					5.0,
					"21/01/2023 10:20:30",
					"security",
					"status",
					"trader",
					"benchmark",
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
		@Tag("TradeServiceTest")
		@DisplayName("test saveTrade should throw UnexpectedRollbackException on any RuntimeException")
		public void saveTradeTestShouldThrowUnexpectedRollbackExceptionOnAnyRuntimeException() {
			
			//GIVEN
			when(tradeRepository.save(any(Trade.class))).thenThrow(new RuntimeException());

			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> tradeService.saveTrade(trade, request))
					.getMessage()).isEqualTo("Error while saving trade");
		}	
	}

	@Nested
	@Tag("deleteTradeTests")
	@DisplayName("Tests for deleting trades")
	@TestInstance(Lifecycle.PER_CLASS)
	class DeleteTradeTests {
		
		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/trade/delete/1");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}
		
		@BeforeEach
		public void setUpForEachTest() {
			trade = new Trade();
			trade.setTradeId(1);
			trade.setAccount("account");
			trade.setType("type");
			trade.setBuyQuantity(3.0);
			trade.setSellQuantity(1.0);
			trade.setBuyPrice(4.0);
			trade.setSellPrice(5.0);
			trade.setTradeDate(LocalDateTime.parse("21/01/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			trade.setSecurity("security");
			trade.setStatus("status");
			trade.setTrader("trader");
			trade.setBenchmark("benchmark");
			trade.setBook("book");
			trade.setCreationName("creation name");
			trade.setCreationDate(LocalDateTime.parse("22/01/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			trade.setRevisionName("revisionName");
			trade.setRevisionDate(LocalDateTime.parse("23/01/2023 13:23:33", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			trade.setDealName("deal name");
			trade.setDealType("deal type");
			trade.setSourceListId("source list id");
			trade.setSide("side");					
		}
		
		@AfterEach
		public void unSetForEachTests() {
			tradeService = null;
			trade = null;
		}

		@Test
		@Tag("TradeServiceTest")
		@DisplayName("test deleteTrade by Id should delete it")
		public void deleteTradeByIdTestShouldDeleteIt() {
			
			//GIVEN
			when(tradeRepository.findById(anyInt())).thenReturn(Optional.of(trade));
			ArgumentCaptor<Trade> tradeBeingDeleted = ArgumentCaptor.forClass(Trade.class);
			doNothing().when(tradeRepository).delete(any(Trade.class));// Needed to Capture trade
			
			//WHEN
			tradeService.deleteTradeById(1, request);
			
			//THEN
			verify(tradeRepository, times(1)).delete(tradeBeingDeleted.capture());
			assertThat(tradeBeingDeleted.getValue()).extracting(
					Trade::getTradeId,
					Trade::getAccount,
					Trade::getType,
					Trade::getBuyQuantity,
					Trade::getSellQuantity,
					Trade::getBuyPrice,
					Trade::getSellPrice,
					tr -> tr.getTradeDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
					Trade::getSecurity,
					Trade::getStatus,
					Trade::getTrader,
					Trade::getBenchmark,
					Trade::getBook,
					Trade::getCreationName,
					tr -> tr.getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
					Trade::getRevisionName,
					tr -> tr.getRevisionDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
					Trade::getDealName,
					Trade::getDealType,
					Trade::getSourceListId,
					Trade::getSide)
			.containsExactly(
					1,
					"account",
					"type",
					3.0,
					1.0,
					4.0,
					5.0,
					"21/01/2023 10:20:30",
					"security",
					"status",
					"trader",
					"benchmark",
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
		@Tag("TradeServiceTest")
		@DisplayName("test deleteTrade by Id by Id should throw UnexpectedRollbackException on UnexpectedRollbackException")
		public void deleteTradeByIdTestShouldThrowUnexpectedRollbackExceptionOnUnexpectedRollbackException() {

			//GIVEN
			when(tradeRepository.findById(anyInt())).thenThrow(new UnexpectedRollbackException("Error while getting trade"));

			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> tradeService.deleteTradeById(2, request))
					.getMessage()).isEqualTo("Error while deleting trade");
		}	

		@Test
		@Tag("TradeServiceTest")
		@DisplayName("test deleteTrade by Id should throw UnexpectedRollbackException On InvalidDataAccessApiUsageException()")
		public void deleteTradeByIdTestShouldThrowsUnexpectedRollbackExceptionOnInvalidDataAccessApiUsageException() {

			//GIVEN
			when(tradeRepository.findById(anyInt())).thenReturn(Optional.of(trade));
			doThrow(new InvalidDataAccessApiUsageException("The given id must not be null")).when(tradeRepository).delete(any(Trade.class));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> tradeService.deleteTradeById(1, request))
					.getMessage()).isEqualTo("Error while deleting trade");
		}	

		
		@Test
		@Tag("TradeServiceTest")
		@DisplayName("test deleteTrade by Id should throw UnexpectedRollbackException On Any RuntimeException")
		public void deleteTradeByIdTestShouldThrowsUnexpectedRollbackExceptionOnAnyRuntimeException() {

			//GIVEN
			when(tradeRepository.findById(anyInt())).thenReturn(Optional.of(trade));
			doThrow(new RuntimeException()).when(tradeRepository).delete(any(Trade.class));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> tradeService.deleteTradeById(1, request))
					.getMessage()).isEqualTo("Error while deleting trade");
		}	
	}
}
