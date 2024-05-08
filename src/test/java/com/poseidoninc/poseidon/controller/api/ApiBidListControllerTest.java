package com.poseidoninc.poseidon.controller.api;

import com.poseidoninc.poseidon.domain.BidList;
import com.poseidoninc.poseidon.exception.BadRequestException;
import com.poseidoninc.poseidon.service.BidListService;
import com.poseidoninc.poseidon.service.RequestService;
import com.poseidoninc.poseidon.service.RequestServiceImpl;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApiBidListControllerTest {

    @InjectMocks
    private ApiBidListController apiBidListController;

    @Mock
    private BidListService bidListService;

    @Spy
    private final RequestService requestService = new RequestServiceImpl();

    private MockHttpServletRequest requestMock;
    private WebRequest request;
    private BidList bidList;

    @AfterEach
    public void unsetForEachTest() {
        bidListService = null;
        apiBidListController = null;
        bidList = null;
    }

    @Nested
    @Tag("getBidLists")
    @DisplayName("Tests for /api/bidList/list")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GetBidListsTests {

        private Pageable pageRequest;

        @BeforeAll
        public void setUpForAllTests() {
            pageRequest = Pageable.unpaged();
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("GET");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/bidList/list");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            pageRequest = null;
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("ApiBidListControllerTest")
        @DisplayName("test getBidLists should return a Success ResponseEntity With Iterable Of BidList")
        public void getBidListsTestShouldReturnSuccessResponseEntityWithIterableOfBidList() {

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

            when(bidListService.getBidLists(any(Pageable.class))).thenReturn(new PageImpl<BidList>(expectedBidLists, pageRequest, 2));

            //WHEN
            ResponseEntity<Iterable<BidList>> responseEntity = apiBidListController.getBidLists(request);

            //THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            Iterable<BidList> resultBidLists = responseEntity.getBody();
            assertThat(resultBidLists).isNotNull();
            assertThat(resultBidLists).containsExactlyElementsOf(expectedBidLists);
        }

        @Test
        @Tag("ApiBidListControllerTest")
        @DisplayName("test getBidLists should throw UnexpectedRollbackException")
        public void getBidListsTestShouldThrowUnexpectedRollbackException() {

            //GIVEN
            when(bidListService.getBidLists(any(Pageable.class))).thenThrow(new UnexpectedRollbackException("Error while getting bidLists"));

            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiBidListController.getBidLists(request))
                    .getMessage()).isEqualTo("Error while getting bidLists");
        }
    }

    @Nested
    @Tag("createBidList")
    @DisplayName("Tests for /api/bidList/create")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class CreateBidListTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("POST");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/bidList/create");
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

        @Test
        @Tag("ApiBidListControllerTest")
        @DisplayName("test createBidList should return a Success ResponseEntity With Saved BidList")
        public void createBidListTestShouldReturnASuccessResponseEntityWithSavedBidList() {

            //GIVEN
            Optional<BidList> optionalBidList = Optional.of(bidList);
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
            when(bidListService.saveBidList(any(BidList.class))).thenReturn(bidListExpected);

            //WHEN
            ResponseEntity<BidList> responseEntity = null;
            try {
                responseEntity = apiBidListController.createBidList(optionalBidList, request);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            BidList resultBidList = responseEntity.getBody();
            assertThat(resultBidList).isNotNull();
            assertThat(resultBidList)
                .extracting(
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
        @Tag("ApiBidListControllerTest")
        @DisplayName("test createBidList should throw BadRequestException")
        public void createBidListTestShouldThrowBadRequestException() {

            //GIVEN
            Optional<BidList> optionalBidList = Optional.empty();

            //WHEN
            //THEN
            assertThat(assertThrows(BadRequestException.class,
                    () -> apiBidListController.createBidList(optionalBidList, request))
                    .getMessage()).isEqualTo("Correct request should be a json BidList body");
        }

        @Test
        @Tag("ApiBidListControllerTest")
        @DisplayName("test createBidList should throw UnexpectedRollbackException")
        public void createBidListTestShouldThrowMUnexpectedRollbackException() {

            //GIVEN
            Optional<BidList> optionalBidList = Optional.of(bidList);
            when(bidListService.saveBidList(any(BidList.class))).thenThrow(new UnexpectedRollbackException("Error while saving bidList"));

            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiBidListController.createBidList(optionalBidList, request))
                    .getMessage()).isEqualTo("Error while saving bidList");
        }
    }

    @Nested
    @Tag("getBidListById")
    @DisplayName("Tests for /api/bidList/update/{id}")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class getBidListByIdTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("GET");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/bidList/update/1");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("ApiBidListControllerTest")
        @DisplayName("test getBidListById should return a Success ResponseEntity With BidList")
        public void getBidListByIdTestShouldReturnASuccessResponseEntityWithBidList() {

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

            when(bidListService.getBidListById(anyInt())).thenReturn(bidList);

            //WHEN
            ResponseEntity<BidList> responseEntity = apiBidListController.getBidListById(1, request);

            //THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            BidList resultBidList = responseEntity.getBody();
            assertThat(resultBidList).isNotNull();
            assertThat(resultBidList)
                    .extracting(
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
        @Tag("ApiBidListControllerTest")
        @DisplayName("test getBidListById should throw UnexpectedRollbackException")
        public void getBidListByIdTestShouldThrowUnexpectedRollbackException() {

            //GIVEN
            when(bidListService.getBidListById(anyInt())).thenThrow(new UnexpectedRollbackException("Error while getting bidList"));
            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiBidListController.getBidListById(1, request))
                    .getMessage()).isEqualTo("Error while getting bidList");
        }
    }

    @Nested
    @Tag("updateBidList")
    @DisplayName("Tests for /api/bidList/update")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class UpdateBidListTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("PUT");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/bidList/update");
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

        @Test
        @Tag("ApiBidListControllerTest")
        @DisplayName("test updateBidList should return a Success ResponseEntity With Saved BidList")
        public void updateBidListTestShouldReturnASuccessResponseEntityWithSavedBidList() {

            //GIVEN
            Optional<BidList> optionalBidList = Optional.of(bidList);
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
            when(bidListService.saveBidList(any(BidList.class))).thenReturn(bidListExpected);

            //WHEN
            ResponseEntity<BidList> responseEntity = null;
            try {
                responseEntity = apiBidListController.updateBidList(optionalBidList, request);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            BidList resultBidList = responseEntity.getBody();
            assertThat(resultBidList).isNotNull();
            assertThat(resultBidList)
                    .extracting(
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
        @Tag("ApiBidListControllerTest")
        @DisplayName("test updateBidList should throw BadRequestException")
        public void updateBidListTestShouldThrowBadRequestException() {

            //GIVEN
            Optional<BidList> optionalBidList = Optional.empty();

            //WHEN
            //THEN
            assertThat(assertThrows(BadRequestException.class,
                    () -> apiBidListController.updateBidList(optionalBidList, request))
                    .getMessage()).isEqualTo("Correct request should be a json BidList body");
        }

        @Test
        @Tag("ApiBidListControllerTest")
        @DisplayName("test updateBidList should throw UnexpectedRollbackException")
        public void updateBidListTestShouldThrowMUnexpectedRollbackException() {

            //GIVEN
            Optional<BidList> optionalBidList = Optional.of(bidList);
            when(bidListService.saveBidList(any(BidList.class))).thenThrow(new UnexpectedRollbackException("Error while saving bidList"));

            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiBidListController.updateBidList(optionalBidList, request))
                    .getMessage()).isEqualTo("Error while saving bidList");
        }
    }

    @Nested
    @Tag("deleteByIdTests")
    @DisplayName("Tests for /api/bidList/delete/{id}")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class DeleteByIdTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("DELETE");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/bidList/delete/1");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("ApiBidListControllerTest")
        @DisplayName("test deleteById should return HttpStatus.OK")
        public void deleteByIdTestShouldReturnHttpStatusOK() {

            //GIVEN
            //WHEN
            HttpStatus httpStatus = apiBidListController.deleteBidListById(1, request);

            //THEN
            assertThat(httpStatus.is2xxSuccessful()).isTrue();
        }

        @Test
        @Tag("ApiBidListControllerTest")
        @DisplayName("test deleteById should throw UnexpectedRollbackException")
        public void deleteByIdTestShouldThrowUnexpectedRollbackException() {

            //GIVEN
            doThrow(new UnexpectedRollbackException("Error while deleting bidList")).when(bidListService).deleteBidListById(anyInt());
            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiBidListController.deleteBidListById(1, request))
                    .getMessage()).isEqualTo("Error while deleting bidList");
        }
    }
}
