package com.poseidoninc.poseidon.controller;

import com.poseidoninc.poseidon.domain.RuleName;
import com.poseidoninc.poseidon.domain.Trade;
import com.poseidoninc.poseidon.service.RequestService;
import com.poseidoninc.poseidon.service.RequestServiceImpl;
import com.poseidoninc.poseidon.service.TradeService;
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
 * unit test class for the TradeController.
 * @author olivier morel
 */
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

    @Spy
    private final RequestService requestService = new RequestServiceImpl();

    private MockHttpServletRequest requestMock;
    private WebRequest request;

    @AfterEach
    public void unsetForEachTest() {
        tradeService = null;
        tradeController = null;
    }

    @Nested
    @Tag("homeTradeControllerTests")
    @DisplayName("Tests for /trade/list")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class HomeTradeControllerTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/trade/list");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("TradeControllerTest")
        @DisplayName("test home should return \"trade/list\"")
        public void homeTestShouldReturnStringTradeList() {

            //GIVEN
            List<Trade> expectedTrades = new ArrayList<>();
            Trade trade = new Trade();
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

            ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<Iterable<Trade>> iterableArgumentCaptor = ArgumentCaptor.forClass(Iterable.class);
            when(tradeService.getTrades(any(Pageable.class))).thenReturn(new PageImpl<Trade>(expectedTrades));

            //WHEN
            String html = tradeController.home(model, request);

            //THEN
            verify(model).addAttribute(stringArgumentCaptor.capture(), iterableArgumentCaptor.capture()); //times(1) is used by default
            assertThat(stringArgumentCaptor.getValue()).isEqualTo("trades");
            assertThat(iterableArgumentCaptor.getValue()).containsExactlyElementsOf(expectedTrades);
            assertThat(html).isEqualTo("trade/list");
        }

        @Test
        @Tag("TradeControllerTest")
        @DisplayName("test home should throw UnexpectedRollbackException")
        public void homeTestShouldThrowUnexpectedRollbackException() {

            //GIVEN
            when(tradeService.getTrades(any(Pageable.class))).thenThrow(new UnexpectedRollbackException("Error while getting tradess"));

            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> tradeController.home(model, request))
                    .getMessage()).isEqualTo("Error while getting trades");
        }
    }

    @Test
    @Tag("TradeControllerTest")
    @DisplayName("test addTradeForm should return \"trade/add\"")
    public void tradeAddTestShouldReturnStringTradeAdd() {

        //GIVEN
        Trade trade = new Trade();

        //WHEN
        String html = tradeController.addTradeForm(trade);

        //THEN
        assertThat(html).isEqualTo("trade/add");
    }

    @Nested
    @Tag("validateTradeControllerTests")
    @DisplayName("Tests for /trade/validate")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ValidateTradeControllerTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/trade/validate/");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("TradeControllerTest")
        @DisplayName("test validate should return \"redirect:/trade/list\"")
        public void validateTestShouldReturnStringRedirectTradeList() {

            //GIVEN
            Trade trade = new Trade();
            when(bindingResult.hasErrors()).thenReturn(false);
            when(tradeService.saveTrade(any(Trade.class))).thenReturn(trade);


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
        @DisplayName("test validate should throw UnexpectedRollbackException")
        public void validateTestShouldThrowUnexpectedRollbackException() {

            //GIVEN
            Trade trade = new Trade();
            when(bindingResult.hasErrors()).thenReturn(false);
            when(tradeService.saveTrade(any(Trade.class))).thenThrow(new UnexpectedRollbackException("Error while saving trade"));
            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> tradeController.validate(trade, bindingResult, request))
                    .getMessage()).isEqualTo("Error while saving trade");
        }
    }

    @Nested
    @Tag("showUpdateFormTradeControllerTests")
    @DisplayName("Tests for /trade/update/{id}")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ShowUpdateFormTradeControllerTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/trade/update/1");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("TradeControllerTest")
        @DisplayName("test showUpdateForm should return \"trade/update\"")
        public void showUpdateFormTestShouldReturnStringTradeUpdate() {

            //GIVEN
            Trade trade = new Trade();
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
            ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<Trade> tradeArgumentCaptor = ArgumentCaptor.forClass(Trade.class);
            when(tradeService.getTradeById(anyInt())).thenReturn(trade);


            //WHEN
            String html = tradeController.showUpdateForm(1, model, request);

            //THEN
            verify(model).addAttribute(stringArgumentCaptor.capture(), tradeArgumentCaptor.capture()); //times(1) is used by default
            assertThat(stringArgumentCaptor.getValue()).isEqualTo("trade");
            assertThat(tradeArgumentCaptor.getValue()).isEqualTo(trade);
            assertThat(html).isEqualTo("trade/update");
        }

        @Test
        @Tag("TradeControllerTest")
        @DisplayName("test showUpdateForm should throw UnexpectedRollbackException")
        public void showUpdateFormTestShouldThrowUnexpectedRollbackException() {

            //GIVEN
            when(tradeService.getTradeById(anyInt())).thenThrow(new UnexpectedRollbackException("Error while getting trade"));
            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> tradeController.showUpdateForm(1, model, request))
                    .getMessage()).isEqualTo("Error while getting trade");
        }
    }

    @Nested
    @Tag("updateTradeTradeControllerTests")
    @DisplayName("Tests for /trade/update")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class updateTradeTradeControllerTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/trade/update/");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("TradeControllerTest")
        @DisplayName("test update Trade should return \"redirect:/trade/list\"")
        public void updateCurveTestShouldReturnStringRedirectTradeList() {

            //GIVEN
            Trade trade = new Trade();
            when(bindingResult.hasErrors()).thenReturn(false);
            when(tradeService.saveTrade(any(Trade.class))).thenReturn(trade);


            //WHEN
            String html = tradeController.updateTrade(trade, bindingResult, request);

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
            String html = tradeController.updateTrade(trade, bindingResult, request);

            //THEN
            assertThat(html).isEqualTo("trade/update");
        }

        @Test
        @Tag("TradeControllerTest")
        @DisplayName("test update Trade should throw UnexpectedRollbackException")
        public void updateTradeTestShouldThrowUnexpectedRollbackException() {

            //GIVEN
            Trade trade = new Trade();
            when(bindingResult.hasErrors()).thenReturn(false);
            when(tradeService.saveTrade(any(Trade.class))).thenThrow(new UnexpectedRollbackException("Error while saving trade"));
            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> tradeController.updateTrade(trade, bindingResult, request))
                    .getMessage()).isEqualTo("Error while saving trade");
        }
    }


    @Nested
    @Tag("deleteTradeTradeControllerTests")
    @DisplayName("Tests for /trade/delete/{id}")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class deleteTradeTradeControllerTests {

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

        @Test
        @Tag("TradeControllerTest")
        @DisplayName("test delete Trade should return \"redirect:/trade/list\"")
        public void deleteCurveTestShouldReturnStringRedirectCurvePointList() {

            //GIVEN
            //WHEN
            String html = tradeController.deleteTrade(1, request);

            //THEN
            assertThat(html).isEqualTo("redirect:/trade/list");
        }

        @Test
        @Tag("TradeControllerTest")
        @DisplayName("test delete Trade should throw UnexpectedRollbackException")
        public void deleteTradeTestShouldThrowUnexpectedRollbackException() {

            //GIVEN
            doThrow(new UnexpectedRollbackException("Error while deleting trade")).when(tradeService).deleteTradeById(anyInt());
            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> tradeController.deleteTrade(1, request))
                    .getMessage()).isEqualTo("Error while deleting trade");
        }
    }
}
