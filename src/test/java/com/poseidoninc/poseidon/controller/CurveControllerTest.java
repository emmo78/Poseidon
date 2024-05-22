package com.poseidoninc.poseidon.controller;

import com.poseidoninc.poseidon.domain.BidList;
import com.poseidoninc.poseidon.domain.CurvePoint;
import com.poseidoninc.poseidon.service.CurvePointService;
import com.poseidoninc.poseidon.service.RequestService;
import com.poseidoninc.poseidon.service.RequestServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
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
 * unit test class for the CurveController.
 * @author olivier morel
 */
@ExtendWith(MockitoExtension.class)
public class CurveControllerTest {

	@InjectMocks
	private CurveController curveController;

	@Mock
	private CurvePointService curvePointService;
	
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
		curvePointService = null;
		curveController = null;
	}

	@Nested
	@Tag("homeCurveControllerTests")
	@DisplayName("Tests for /curvePoint/list")
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class HomeCurveControllerTests {

		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/curvePoint/list");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}

		@Test
		@Tag("CurveControllerTest")
		@DisplayName("test home should return \"curvePoint/list\"")
		public void homeTestShouldReturnStringCurvePointList() {

			//GIVEN
			List<CurvePoint> expectedCurvePoints = new ArrayList<>();
			CurvePoint curvePoint = new CurvePoint();
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

			ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
			ArgumentCaptor<Iterable<CurvePoint>> iterableArgumentCaptor = ArgumentCaptor.forClass(Iterable.class);
			when(curvePointService.getCurvePoints(any(Pageable.class))).thenReturn(new PageImpl<CurvePoint>(expectedCurvePoints));

			//WHEN
			String html = curveController.home(model, request);

			//THEN
			verify(model).addAttribute(stringArgumentCaptor.capture(), iterableArgumentCaptor.capture()); //times(1) is used by default
			assertThat(stringArgumentCaptor.getValue()).isEqualTo("curvePoints");
			assertThat(iterableArgumentCaptor.getValue()).containsExactlyElementsOf(expectedCurvePoints);
			assertThat(html).isEqualTo("curvePoint/list");
		}

		@Test
		@Tag("CurveControllerTest")
		@DisplayName("test home should throw UnexpectedRollbackException")
		public void homeTestShouldThrowUnexpectedRollbackException() {

			//GIVEN
			when(curvePointService.getCurvePoints(any(Pageable.class))).thenThrow(new UnexpectedRollbackException("Error while getting curvePoints"));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> curveController.home(model, request))
					.getMessage()).isEqualTo("Error while getting curvePoints");
		}
	}

	@Test
	@Tag("CurveControllerTest")
	@DisplayName("test addCurvePoint should return \"curvePoint/add\"")
	public void curvePointAddTestShouldReturnStringCurvePointAdd() {

		//GIVEN
		CurvePoint curvePoint = new CurvePoint();

		//WHEN
		String html = curveController.addCurvePoint(curvePoint);

		//THEN
		assertThat(html).isEqualTo("curvePoint/add");
	}

	@Nested
	@Tag("validateCurveControllerTests")
	@DisplayName("Tests for /curvePoint/validate")
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class ValidateCurveControllerTests {

		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/curvePoint/validate/");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}

		@Test
		@Tag("CurveControllerTest")
		@DisplayName("test validate should return \"redirect:/curvePoint/list\"")
		public void validateTestShouldReturnStringRedirectCurvePointList() {

			//GIVEN
			CurvePoint curvePoint = new CurvePoint();
			when(bindingResult.hasErrors()).thenReturn(false);
			when(curvePointService.saveCurvePoint(any(CurvePoint.class))).thenReturn(curvePoint);

			//WHEN
			String html = curveController.validate(curvePoint, bindingResult, request);

			//THEN
			assertThat(html).isEqualTo("redirect:/curvePoint/list");
		}

		@Test
		@Tag("CurveControllerTest")
		@DisplayName("test validate should return \"curvePoint/add\" on BindingResultError")
		public void validateTestShouldReturnStringCurvePointAddOnBindingResulError() {

			//GIVEN
			CurvePoint curvePoint = new CurvePoint();
			when(bindingResult.hasErrors()).thenReturn(true);

			//WHEN
			String html = curveController.validate(curvePoint, bindingResult, request);

			//THEN
			assertThat(html).isEqualTo("curvePoint/add");
		}

		@Test
		@Tag("CurveControllerTest")
		@DisplayName("test validate should return \"curvePoint/add\" on DataIntegrityViolationException")
		public void validateTestShouldReturnStringCurvePointAddDataIntegrityViolationException() {

			//GIVEN
			CurvePoint curvePoint = new CurvePoint();
			when(bindingResult.hasErrors()).thenReturn(false);
			when(curvePointService.saveCurvePoint(any(CurvePoint.class))).thenThrow(new DataIntegrityViolationException(""));

			//WHEN
			String html = curveController.validate(curvePoint, bindingResult, request);

			//THEN
			assertThat(html).isEqualTo("curvePoint/add");
		}

		@Test
		@Tag("CurveControllerTest")
		@DisplayName("test validate should throw UnexpectedRollbackException")
		public void validateTestShouldThrowUnexpectedRollbackException() {

			//GIVEN
			CurvePoint curvePoint = new CurvePoint();
			when(bindingResult.hasErrors()).thenReturn(false);
			when(curvePointService.saveCurvePoint(any(CurvePoint.class))).thenThrow(new UnexpectedRollbackException("Error while saving curvePoint"));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> curveController.validate(curvePoint, bindingResult, request))
					.getMessage()).isEqualTo("Error while saving curvePoint");
		}
	}

	@Nested
	@Tag("showUpdateFormCurveControllerTests")
	@DisplayName("Tests for /curvePoint/update/{id}")
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class ShowUpdateFormCurveControllerTests {

		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/curvePoint/update/1");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}

		@Test
		@Tag("CurveControllerTest")
		@DisplayName("test showUpdateForm should return \"curvePoint/update\"")
		public void showUpdateFormTestShouldReturnStringCurvePointUpdate() {

			//GIVEN
			CurvePoint curvePoint = new CurvePoint();
			curvePoint.setId(1);
			curvePoint.setCurveId(2);
			curvePoint.setAsOfDate(LocalDateTime.parse("21/01/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			curvePoint.setTerm(3.0);
			curvePoint.setValue(4.0);
			curvePoint.setCreationDate(LocalDateTime.parse("22/01/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
			ArgumentCaptor<CurvePoint> curvePointArgumentCaptor = ArgumentCaptor.forClass(CurvePoint.class);
			when(curvePointService.getCurvePointById(anyInt())).thenReturn(curvePoint);

			//WHEN
			String html = curveController.showUpdateForm(1, model, request);

			//THEN
			verify(model).addAttribute(stringArgumentCaptor.capture(), curvePointArgumentCaptor.capture()); //times(1) is used by default
			assertThat(stringArgumentCaptor.getValue()).isEqualTo("curvePoint");
			assertThat(curvePointArgumentCaptor.getValue()).isEqualTo(curvePoint);
			assertThat(html).isEqualTo("curvePoint/update");
		}

		@Test
		@Tag("CurveControllerTest")
		@DisplayName("test showUpdateForm should throw UnexpectedRollbackException")
		public void showUpdateFormTestShouldThrowUnexpectedRollbackException() {

			//GIVEN
			when(curvePointService.getCurvePointById(anyInt())).thenThrow(new UnexpectedRollbackException("Error while getting curvePoint"));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> curveController.showUpdateForm(1, model, request))
					.getMessage()).isEqualTo("Error while getting curvePoint");
		}
	}

	@Nested
	@Tag("updateCurvePointCurveControllerTests")
	@DisplayName("Tests for /curvePoint/update")
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class updateCurvePointCurveControllerTests {

		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/curvePoint/update/");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}

		@Test
		@Tag("CurveControllerTest")
		@DisplayName("test update CurvePoint should return \"redirect:/curvePoint/list\"")
		public void updateCurvePointTestShouldReturnStringRedirectCurvePointList() {

			//GIVEN
			CurvePoint curvePoint = new CurvePoint();
			when(bindingResult.hasErrors()).thenReturn(false);
			when(curvePointService.saveCurvePoint(any(CurvePoint.class))).thenReturn(curvePoint);

			//WHEN
			String html = curveController.updateCurvePoint(curvePoint, bindingResult, request);

			//THEN
			assertThat(html).isEqualTo("redirect:/curvePoint/list");
		}

		@Test
		@Tag("CurveControllerTest")
		@DisplayName("test update CurvePoint should return \"curvePoint/update\" on BindingResultError")
		public void updateCurvePointTestShouldReturnStringCurvePointUpdateOnBindingResulError() {

			//GIVEN
			CurvePoint curvePoint = new CurvePoint();
			when(bindingResult.hasErrors()).thenReturn(true);

			//WHEN
			String html = curveController.updateCurvePoint(curvePoint, bindingResult, request);

			//THEN
			assertThat(html).isEqualTo("curvePoint/update");
		}

		@Test
		@Tag("CurveControllerTest")
		@DisplayName("test update CurvePoint should return \"curvePoint/update\" on DataIntegrityViolationException")
		public void updateCurvePointTestShouldReturnStringCurvePointUpdateDataIntegrityViolationException() {

			//GIVEN
			CurvePoint curvePoint = new CurvePoint();
			when(bindingResult.hasErrors()).thenReturn(false);
			when(curvePointService.saveCurvePoint(any(CurvePoint.class))).thenThrow(new DataIntegrityViolationException(""));

			//WHEN
			String html = curveController.updateCurvePoint(curvePoint, bindingResult, request);

			//THEN
			assertThat(html).isEqualTo("curvePoint/update");
		}

		@Test
		@Tag("CurveControllerTest")
		@DisplayName("test update CurvePoint should throw UnexpectedRollbackException")
		public void updateCurvePointTestShouldThrowUnexpectedRollbackException() {

			//GIVEN
			CurvePoint curvePoint = new CurvePoint();
			when(bindingResult.hasErrors()).thenReturn(false);
			when(curvePointService.saveCurvePoint(any(CurvePoint.class))).thenThrow(new UnexpectedRollbackException("Error while saving curvePoint"));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> curveController.updateCurvePoint(curvePoint, bindingResult, request))
					.getMessage()).isEqualTo("Error while saving curvePoint");
		}
	}

	@Nested
	@Tag("deleteCurvePointCurveControllerTests")
	@DisplayName("Tests for /curvePoint/delete/{id}")
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class deleteCurvePointCurveControllerTests {

		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/curvePoint/delete/1");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}

		@Test
		@Tag("CurveControllerTest")
		@DisplayName("test delete CurvePoint should return \"redirect:/curvePoint/list\"")
		public void deleteCurvePointTestShouldReturnStringRedirectCurvePointList() {

			//GIVEN
			//WHEN
			String html = curveController.deleteCurvePoint(1, request);

			//THEN
			assertThat(html).isEqualTo("redirect:/curvePoint/list");
		}

		@Test
		@Tag("CurveControllerTest")
		@DisplayName("test delete CurvePoint should throw UnexpectedRollbackException")
		public void deleteCurvePointTestShouldThrowUnexpectedRollbackException() {

			//GIVEN
			doThrow(new UnexpectedRollbackException("Error while deleting curvePoint")).when(curvePointService).deleteCurvePointById(anyInt());
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> curveController.deleteCurvePoint(1, request))
					.getMessage()).isEqualTo("Error while deleting curvePoint");
		}
	}
}
