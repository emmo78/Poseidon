package com.poseidoninc.poseidon.controller;

import com.poseidoninc.poseidon.domain.Trade;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
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
            when(tradeService.getTrades(any(Pageable.class))).thenReturn(new PageImpl<Trade>(new ArrayList<>()));

            //WHEN
            String html = tradeController.home(model, request);

            //THEN
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
            when(tradeService.getTradeById(anyInt())).thenReturn(trade);


            //WHEN
            String html = tradeController.showUpdateForm(1, model, request);

            //THEN
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
