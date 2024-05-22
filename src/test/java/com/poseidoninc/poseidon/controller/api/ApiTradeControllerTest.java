package com.poseidoninc.poseidon.controller.api;

import com.poseidoninc.poseidon.domain.Trade;
import com.poseidoninc.poseidon.exception.BadRequestException;
import com.poseidoninc.poseidon.service.RequestService;
import com.poseidoninc.poseidon.service.RequestServiceImpl;
import com.poseidoninc.poseidon.service.TradeService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * unit test class for the ApiTradeController.
 * @author olivier morel
 */
@ExtendWith(MockitoExtension.class)
public class ApiTradeControllerTest {

    @InjectMocks
    private ApiTradeController apiTradeController;

    @Mock
    private TradeService tradeService;

    @Spy
    private final RequestService requestService = new RequestServiceImpl();

    private MockHttpServletRequest requestMock;
    private WebRequest request;
    private Trade trade;

    @AfterEach
    public void unsetForEachTest() {
        tradeService = null;
        apiTradeController = null;
        trade = null;
    }

    @Nested
    @Tag("getTrades")
    @DisplayName("Tests for /api/trade/list")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GetTradesTests {

        private Pageable pageRequest;

        @BeforeAll
        public void setUpForAllTests() {
            pageRequest = Pageable.unpaged();
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("GET");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/trade/list");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            pageRequest = null;
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("ApiTradeControllerTest")
        @DisplayName("test getTrades should return a Success ResponseEntity With Iterable Of Trade")
        public void getTradesTestShouldReturnSuccessResponseEntityWithIterableOfTrade() {

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

            when(tradeService.getTrades(any(Pageable.class))).thenReturn(new PageImpl<Trade>(expectedTrades, pageRequest, 2));

            //WHEN
            ResponseEntity<Iterable<Trade>> responseEntity = apiTradeController.getTrades(request);

            //THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            Iterable<Trade> resultTrades = responseEntity.getBody();
            assertThat(resultTrades).isNotNull();
            assertThat(resultTrades).containsExactlyElementsOf(expectedTrades);
        }

        @Test
        @Tag("ApiTradeControllerTest")
        @DisplayName("test getTrades should throw UnexpectedRollbackException")
        public void getTradesTestShouldThrowUnexpectedRollbackException() {

            //GIVEN
            when(tradeService.getTrades(any(Pageable.class))).thenThrow(new UnexpectedRollbackException("Error while getting trades"));

            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiTradeController.getTrades(request))
                    .getMessage()).isEqualTo("Error while getting trades");
        }
    }

    @Nested
    @Tag("createTrade")
    @DisplayName("Tests for /api/trade/create")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class CreateTradeTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("POST");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/trade/create");
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

        @Test
        @Tag("ApiTradeControllerTest")
        @DisplayName("test createTrade should return a Success ResponseEntity With Saved Trade")
        public void createTradeTestShouldReturnASuccessResponseEntityWithSavedTrade() {

            //GIVEN
            Optional<Trade> optionalTrade = Optional.of(trade);
            Trade tradeExpected = new Trade();
            tradeExpected.setTradeId(1);
            tradeExpected.setAccount("account");
            tradeExpected.setType("type");
            tradeExpected.setBuyQuantity(3.0);
            tradeExpected.setSellQuantity(1.0);
            tradeExpected.setBuyPrice(4.0);
            tradeExpected.setSellPrice(5.0);
            tradeExpected.setTradeDate(LocalDateTime.parse("21/01/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            tradeExpected.setSecurity("security");
            tradeExpected.setStatus("status");
            tradeExpected.setTrader("trader");
            tradeExpected.setBenchmark("benchmark");
            tradeExpected.setBook("book");
            tradeExpected.setCreationName("creation name");
            tradeExpected.setCreationDate(LocalDateTime.parse("22/01/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            tradeExpected.setRevisionName("revisionName");
            tradeExpected.setRevisionDate(LocalDateTime.parse("23/01/2023 13:23:33", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            tradeExpected.setDealName("deal name");
            tradeExpected.setDealType("deal type");
            tradeExpected.setSourceListId("source list id");
            tradeExpected.setSide("side");
            when(tradeService.saveTrade(any(Trade.class))).thenReturn(tradeExpected);

            //WHEN
            ResponseEntity<Trade> responseEntity = null;
            try {
                responseEntity = apiTradeController.createTrade(optionalTrade, request);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            Trade resultTrade = responseEntity.getBody();
            assertThat(resultTrade).isNotNull();
            assertThat(resultTrade)
                    .extracting(
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
        @Tag("ApiTradeControllerTest")
        @DisplayName("test createTrade should throw BadRequestException")
        public void createTradeTestShouldThrowBadRequestException() {

            //GIVEN
            Optional<Trade> optionalTrade = Optional.empty();

            //WHEN
            //THEN
            assertThat(assertThrows(BadRequestException.class,
                    () -> apiTradeController.createTrade(optionalTrade, request))
                    .getMessage()).isEqualTo("Correct request should be a json Trade body");
        }

        @Test
        @Tag("ApiTradeControllerTest")
        @DisplayName("test createTrade should throw UnexpectedRollbackException")
        public void createTradeTestShouldThrowMUnexpectedRollbackException() {

            //GIVEN
            Optional<Trade> optionalTrade = Optional.of(trade);
            when(tradeService.saveTrade(any(Trade.class))).thenThrow(new UnexpectedRollbackException("Error while saving trade"));

            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiTradeController.createTrade(optionalTrade, request))
                    .getMessage()).isEqualTo("Error while saving trade");
        }
    }

    @Nested
    @Tag("getTradeById")
    @DisplayName("Tests for /api/trade/update/{id}")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class getTradeByIdTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("GET");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/trade/update/1");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("ApiTradeControllerTest")
        @DisplayName("test getTradeById should return a Success ResponseEntity With Trade")
        public void getTradeByIdTestShouldReturnASuccessResponseEntityWithTrade() {

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

            when(tradeService.getTradeById(anyInt())).thenReturn(trade);

            //WHEN
            ResponseEntity<Trade> responseEntity = apiTradeController.getTradeById(1, request);

            //THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            Trade resultTrade = responseEntity.getBody();
            assertThat(resultTrade).isNotNull();
            assertThat(resultTrade)
                    .extracting(
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
        @Tag("ApiTradeControllerTest")
        @DisplayName("test getTradeById should throw UnexpectedRollbackException")
        public void getTradeByIdTestShouldThrowUnexpectedRollbackException() {

            //GIVEN
            when(tradeService.getTradeById(anyInt())).thenThrow(new UnexpectedRollbackException("Error while getting trade"));
            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiTradeController.getTradeById(1, request))
                    .getMessage()).isEqualTo("Error while getting trade");
        }
    }

    @Nested
    @Tag("updateTrade")
    @DisplayName("Tests for /api/trade/update")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class UpdateTradeTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("PUT");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/trade/update");
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

        @Test
        @Tag("ApiTradeControllerTest")
        @DisplayName("test updateTrade should return a Success ResponseEntity With Saved trade")
        public void updateTradeTestShouldReturnASuccessResponseEntityWithSavedTrade() {

            //GIVEN
            Optional<Trade> optionalTrade = Optional.of(trade);
            Trade tradeExpected = new Trade();
            tradeExpected.setTradeId(1);
            tradeExpected.setAccount("account");
            tradeExpected.setType("type");
            tradeExpected.setBuyQuantity(3.0);
            tradeExpected.setSellQuantity(1.0);
            tradeExpected.setBuyPrice(4.0);
            tradeExpected.setSellPrice(5.0);
            tradeExpected.setTradeDate(LocalDateTime.parse("21/01/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            tradeExpected.setSecurity("security");
            tradeExpected.setStatus("status");
            tradeExpected.setTrader("trader");
            tradeExpected.setBenchmark("benchmark");
            tradeExpected.setBook("book");
            tradeExpected.setCreationName("creation name");
            tradeExpected.setCreationDate(LocalDateTime.parse("22/01/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            tradeExpected.setRevisionName("revisionName");
            tradeExpected.setRevisionDate(LocalDateTime.parse("23/01/2023 13:23:33", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            tradeExpected.setDealName("deal name");
            tradeExpected.setDealType("deal type");
            tradeExpected.setSourceListId("source list id");
            tradeExpected.setSide("side");
            when(tradeService.saveTrade(any(Trade.class))).thenReturn(tradeExpected);

            //WHEN
            ResponseEntity<Trade> responseEntity = null;
            try {
                responseEntity = apiTradeController.updateTrade(optionalTrade, request);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            Trade resultTrade = responseEntity.getBody();
            assertThat(resultTrade).isNotNull();
            assertThat(resultTrade)
                    .extracting(
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
        @Tag("ApiTradeControllerTest")
        @DisplayName("test updateTrade should throw BadRequestException")
        public void updateTradeTestShouldThrowBadRequestException() {

            //GIVEN
            Optional<Trade> optionalTrade = Optional.empty();

            //WHEN
            //THEN
            assertThat(assertThrows(BadRequestException.class,
                    () -> apiTradeController.updateTrade(optionalTrade, request))
                    .getMessage()).isEqualTo("Correct request should be a json Trade body");
        }

        @Test
        @Tag("ApiTradeControllerTest")
        @DisplayName("test updateTrade should throw UnexpectedRollbackException")
        public void updateTradeTestShouldThrowMUnexpectedRollbackException() {

            //GIVEN
            Optional<Trade> optionalTrade = Optional.of(trade);
            when(tradeService.saveTrade(any(Trade.class))).thenThrow(new UnexpectedRollbackException("Error while saving trade"));

            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiTradeController.updateTrade(optionalTrade, request))
                    .getMessage()).isEqualTo("Error while saving trade");
        }
    }

    @Nested
    @Tag("deleteByIdTests")
    @DisplayName("Tests for /api/trade/delete/{id}")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class DeleteByIdTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("DELETE");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/trade/delete/1");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("ApiTradeControllerTest")
        @DisplayName("test deleteById should return HttpStatus.OK")
        public void deleteByIdTestShouldReturnHttpStatusOK() {

            //GIVEN
            //WHEN
            HttpStatus httpStatus = apiTradeController.deleteTradeById(1, request);

            //THEN
            assertThat(httpStatus.is2xxSuccessful()).isTrue();
        }

        @Test
        @Tag("ApiTradeControllerTest")
        @DisplayName("test deleteById should throw UnexpectedRollbackException")
        public void deleteByIdTestShouldThrowUnexpectedRollbackException() {

            //GIVEN
            doThrow(new UnexpectedRollbackException("Error while deleting trade")).when(tradeService).deleteTradeById(anyInt());
            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiTradeController.deleteTradeById(1, request))
                    .getMessage()).isEqualTo("Error while deleting trade");
        }
    }
}
