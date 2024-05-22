package com.poseidoninc.poseidon.controller.api;

import com.poseidoninc.poseidon.domain.CurvePoint;
import com.poseidoninc.poseidon.exception.BadRequestException;
import com.poseidoninc.poseidon.service.CurvePointService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * unit test class for the ApiCurveController.
 * @author olivier morel
 */
@ExtendWith(MockitoExtension.class)
public class ApiCurveControllerTest {

    @InjectMocks
    private ApiCurveController apiCurveController;

    @Mock
    private CurvePointService curvePointService;

    @Spy
    private final RequestService requestService = new RequestServiceImpl();

    private MockHttpServletRequest requestMock;
    private WebRequest request;
    private CurvePoint curvePoint;

    @AfterEach
    public void unsetForEachTest() {
        curvePointService = null;
        apiCurveController = null;
        curvePoint = null;
    }

    @Nested
    @Tag("getCurvePoints")
    @DisplayName("Tests for /api/curvePoint/list")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GetCurvePointsTests {

        private Pageable pageRequest;

        @BeforeAll
        public void setUpForAllTests() {
            pageRequest = Pageable.unpaged();
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("GET");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/curvePoint/list");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            pageRequest = null;
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("ApiCurveControllerTest")
        @DisplayName("test getCurvePoints should return a Success ResponseEntity With Iterable Of CurvePoint")
        public void getCurvePointsTestShouldReturnSuccessResponseEntityWithIterableOfCurvePoint() {

            //GIVEN
            List<CurvePoint> expectedCurvePoints = new ArrayList<>();
            curvePoint = new CurvePoint();
			curvePoint.setId(1);
			curvePoint.setCurveId(2);
			curvePoint.setAsOfDate(LocalDateTime.parse("21/01/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			curvePoint.setTerm(3.0);
			curvePoint.setValue(4.0);
			curvePoint.setCreationDate(LocalDateTime.parse("22/01/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            expectedCurvePoints.add(curvePoint);

            CurvePoint curvePoint2 = new CurvePoint();
            curvePoint2.setId(2);
            curvePoint2.setCurveId(3);
            curvePoint2.setAsOfDate(LocalDateTime.parse("23/01/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            curvePoint2.setTerm(4.0);
            curvePoint2.setValue(5.0);
            curvePoint2.setCreationDate(LocalDateTime.parse("24/01/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            expectedCurvePoints.add(curvePoint2);

            when(curvePointService.getCurvePoints(any(Pageable.class))).thenReturn(new PageImpl<CurvePoint>(expectedCurvePoints, pageRequest, 2));

            //WHEN
            ResponseEntity<Iterable<CurvePoint>> responseEntity = apiCurveController.getCurvePoints(request);

            //THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            Iterable<CurvePoint> resultCurvePoints = responseEntity.getBody();
            assertThat(resultCurvePoints).isNotNull();
            assertThat(resultCurvePoints).containsExactlyElementsOf(expectedCurvePoints);
        }

        @Test
        @Tag("ApiCurveControllerTest")
        @DisplayName("test getCurvePoints should throw UnexpectedRollbackException")
        public void getCurvePointsTestShouldThrowUnexpectedRollbackException() {

            //GIVEN
            when(curvePointService.getCurvePoints(any(Pageable.class))).thenThrow(new UnexpectedRollbackException("Error while getting curvePoints"));

            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiCurveController.getCurvePoints(request))
                    .getMessage()).isEqualTo("Error while getting curvePoints");
        }
    }

    @Nested
    @Tag("createCurvePoint")
    @DisplayName("Tests for /api/curvePoint/create")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class CreateCurvePointTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("POST");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/curvePoint/create");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @BeforeEach
        public void setUpForEachTest() {
            curvePoint = new CurvePoint();
            curvePoint.setCurveId(2);
			curvePoint.setAsOfDate(LocalDateTime.parse("21/01/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			curvePoint.setTerm(3.0);
			curvePoint.setValue(4.0);
			curvePoint.setCreationDate(LocalDateTime.parse("22/01/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
	        }

        @Test
        @Tag("ApiCurveControllerTest")
        @DisplayName("test createCurvePoint should return a Success ResponseEntity With Saved CurvePoint")
        public void createCurvePointTestShouldReturnASuccessResponseEntityWithSavedCurvePoint() {

            //GIVEN
            Optional<CurvePoint> optionalCurvePoint = Optional.of(curvePoint);
            CurvePoint curvePointExpected = new CurvePoint();
			curvePointExpected.setId(1);
			curvePointExpected.setCurveId(2);
			curvePointExpected.setAsOfDate(LocalDateTime.parse("21/01/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			curvePointExpected.setTerm(3.0);
			curvePointExpected.setValue(4.0);
			curvePointExpected.setCreationDate(LocalDateTime.parse("22/01/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            when(curvePointService.saveCurvePoint(any(CurvePoint.class))).thenReturn(curvePointExpected);

            //WHEN
            ResponseEntity<CurvePoint> responseEntity = null;
            try {
                responseEntity = apiCurveController.createCurvePoint(optionalCurvePoint, request);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            CurvePoint resultCurvePoint = responseEntity.getBody();
            assertThat(resultCurvePoint).isNotNull();
            assertThat(resultCurvePoint)
                    .extracting(
                            CurvePoint::getId,
                            CurvePoint::getCurveId,
                            curve -> curve.getAsOfDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                            CurvePoint::getTerm,
                            CurvePoint::getValue,
                            curve -> curve.getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                    .containsExactly(
                            1,
                            2,
                            "21/01/2023 10:20:30",
                            3.0,
                            4.0,
                            "22/01/2023 12:22:32"
	                    );
        }

        @Test
        @Tag("ApiCurveControllerTest")
        @DisplayName("test createCurvePoint should throw BadRequestException")
        public void createCurvePointTestShouldThrowBadRequestException() {

            //GIVEN
            Optional<CurvePoint> optionalCurvePoint = Optional.empty();

            //WHEN
            //THEN
            assertThat(assertThrows(BadRequestException.class,
                    () -> apiCurveController.createCurvePoint(optionalCurvePoint, request))
                    .getMessage()).isEqualTo("Correct request should be a json CurvePoint body");
        }

        @Test
        @Tag("ApiCurveControllerTest")
        @DisplayName("test createCurvePoint should throw UnexpectedRollbackException")
        public void createCurvePointTestShouldThrowMUnexpectedRollbackException() {

            //GIVEN
            Optional<CurvePoint> optionalCurvePoint = Optional.of(curvePoint);
            when(curvePointService.saveCurvePoint(any(CurvePoint.class))).thenThrow(new UnexpectedRollbackException("Error while saving curvePoint"));

            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiCurveController.createCurvePoint(optionalCurvePoint, request))
                    .getMessage()).isEqualTo("Error while saving curvePoint");
        }
    }

    @Nested
    @Tag("getCurvePointById")
    @DisplayName("Tests for /api/curvePoint/update/{id}")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class getCurvePointByIdTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("GET");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/curvePoint/update/1");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("ApiCurveControllerTest")
        @DisplayName("test getCurvePointById should return a Success ResponseEntity With CurvePoint")
        public void getCurvePointByIdTestShouldReturnASuccessResponseEntityWithCurvePoint() {

            //GIVEN
            curvePoint = new CurvePoint();
			curvePoint.setId(1);
			curvePoint.setCurveId(2);
			curvePoint.setAsOfDate(LocalDateTime.parse("21/01/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			curvePoint.setTerm(3.0);
			curvePoint.setValue(4.0);
			curvePoint.setCreationDate(LocalDateTime.parse("22/01/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

            when(curvePointService.getCurvePointById(anyInt())).thenReturn(curvePoint);

            //WHEN
            ResponseEntity<CurvePoint> responseEntity = apiCurveController.getCurvePointById(1, request);

            //THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            CurvePoint resultCurvePoint = responseEntity.getBody();
            assertThat(resultCurvePoint).isNotNull();
            assertThat(resultCurvePoint)
                    .extracting(
                            CurvePoint::getId,
                            CurvePoint::getCurveId,
                            curve -> curve.getAsOfDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                            CurvePoint::getTerm,
                            CurvePoint::getValue,
                            curve -> curve.getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                    .containsExactly(
                            1,
                            2,
                            "21/01/2023 10:20:30",
                            3.0,
                            4.0,
                            "22/01/2023 12:22:32"
                    );
        }

        @Test
        @Tag("ApiCurveControllerTest")
        @DisplayName("test getCurvePointById should throw UnexpectedRollbackException")
        public void getCurvePointByIdTestShouldThrowUnexpectedRollbackException() {

            //GIVEN
            when(curvePointService.getCurvePointById(anyInt())).thenThrow(new UnexpectedRollbackException("Error while getting curvePoint"));
            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiCurveController.getCurvePointById(1, request))
                    .getMessage()).isEqualTo("Error while getting curvePoint");
        }
    }

    @Nested
    @Tag("updateCurvePoint")
    @DisplayName("Tests for /api/curvePoint/update")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class UpdateCurvePointTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("PUT");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/curvePoint/update");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @BeforeEach
        public void setUpForEachTest() {
            curvePoint = new CurvePoint();
			curvePoint.setId(1);
			curvePoint.setCurveId(2);
			curvePoint.setAsOfDate(LocalDateTime.parse("21/01/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			curvePoint.setTerm(3.0);
			curvePoint.setValue(4.0);
			curvePoint.setCreationDate(LocalDateTime.parse("22/01/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        }

        @Test
        @Tag("ApiCurveControllerTest")
        @DisplayName("test updateCurvePoint should return a Success ResponseEntity With Saved curvePoint")
        public void updateCurvePointTestShouldReturnASuccessResponseEntityWithSavedCurvePoint() {

            //GIVEN
            Optional<CurvePoint> optionalCurvePoint = Optional.of(curvePoint);
            CurvePoint curvePointExpected = new CurvePoint();
            curvePointExpected.setId(1);
            curvePointExpected.setCurveId(2);
            curvePointExpected.setAsOfDate(LocalDateTime.parse("21/01/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            curvePointExpected.setTerm(3.0);
            curvePointExpected.setValue(4.0);
            curvePointExpected.setCreationDate(LocalDateTime.parse("22/01/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            when(curvePointService.saveCurvePoint(any(CurvePoint.class))).thenReturn(curvePointExpected);

            //WHEN
            ResponseEntity<CurvePoint> responseEntity = null;
            try {
                responseEntity = apiCurveController.updateCurvePoint(optionalCurvePoint, request);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            CurvePoint resultCurvePoint = responseEntity.getBody();
            assertThat(resultCurvePoint).isNotNull();
            assertThat(resultCurvePoint)
                    .extracting(
                            CurvePoint::getId,
                            CurvePoint::getCurveId,
                            curve -> curve.getAsOfDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                            CurvePoint::getTerm,
                            CurvePoint::getValue,
                            curve -> curve.getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                    .containsExactly(
                            1,
                            2,
                            "21/01/2023 10:20:30",
                            3.0,
                            4.0,
                            "22/01/2023 12:22:32"
                    );
        }

        @Test
        @Tag("ApiCurveControllerTest")
        @DisplayName("test updateCurvePoint should throw BadRequestException")
        public void updateCurvePointTestShouldThrowBadRequestException() {

            //GIVEN
            Optional<CurvePoint> optionalCurvePoint = Optional.empty();

            //WHEN
            //THEN
            assertThat(assertThrows(BadRequestException.class,
                    () -> apiCurveController.updateCurvePoint(optionalCurvePoint, request))
                    .getMessage()).isEqualTo("Correct request should be a json CurvePoint body");
        }

        @Test
        @Tag("ApiCurveControllerTest")
        @DisplayName("test updateCurvePoint should throw UnexpectedRollbackException")
        public void updateCurvePointTestShouldThrowMUnexpectedRollbackException() {

            //GIVEN
            Optional<CurvePoint> optionalCurvePoint = Optional.of(curvePoint);
            when(curvePointService.saveCurvePoint(any(CurvePoint.class))).thenThrow(new UnexpectedRollbackException("Error while saving curvePoint"));

            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiCurveController.updateCurvePoint(optionalCurvePoint, request))
                    .getMessage()).isEqualTo("Error while saving curvePoint");
        }
    }

    @Nested
    @Tag("deleteByIdTests")
    @DisplayName("Tests for /api/curvePoint/delete/{id}")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class DeleteByIdTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("DELETE");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/curvePoint/delete/1");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("ApiCurveControllerTest")
        @DisplayName("test deleteById should return HttpStatus.OK")
        public void deleteByIdTestShouldReturnHttpStatusOK() {

            //GIVEN
            //WHEN
            HttpStatus httpStatus = apiCurveController.deleteCurvePointById(1, request);

            //THEN
            assertThat(httpStatus.is2xxSuccessful()).isTrue();
        }

        @Test
        @Tag("ApiCurveControllerTest")
        @DisplayName("test deleteById should throw UnexpectedRollbackException")
        public void deleteByIdTestShouldThrowUnexpectedRollbackException() {

            //GIVEN
            doThrow(new UnexpectedRollbackException("Error while deleting curvePoint")).when(curvePointService).deleteCurvePointById(anyInt());
            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiCurveController.deleteCurvePointById(1, request))
                    .getMessage()).isEqualTo("Error while deleting curvePoint");
        }
    }
}
