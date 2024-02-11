package com.poseidoninc.poseidon.controller;

import com.poseidoninc.poseidon.domain.CurvePoint;
import com.poseidoninc.poseidon.service.CurvePointService;
import com.poseidoninc.poseidon.service.RequestService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

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

	@Mock
	private WebRequest request;

	@Mock
	private RequestService requestService;

	@AfterEach
	public void unsetForEachTest() {
		curvePointService = null;
		curveController = null;
	}

	@Nested
	@Tag("homeCurveControllerTests")
	@DisplayName("Tests for /curvePoint/list")
	class HomeCurveControllerTests {
		@Test
		@Tag("CurveControllerTest")
		@DisplayName("test home should return \"curvePoint/list\"")
		public void homeTestShouldReturnStringCurvePointList() {

			//GIVEN
			when(curvePointService.getCurvePoints(any(Pageable.class))).thenReturn(new PageImpl<CurvePoint>(new ArrayList<>()));

			//WHEN
			String html = curveController.home(model, request);

			//THEN
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
	class ValidateCurveControllerTests {

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
	class ShowUpdateFormCurveControllerTests {

		@Test
		@Tag("CurveControllerTest")
		@DisplayName("test showUpdateForm should return \"curvePoint/update\"")
		public void showUpdateFormTestShouldReturnStringCurvePointUpdate() {

			//GIVEN
			CurvePoint curvePoint = new CurvePoint();
			when(curvePointService.getCurvePointById(anyInt())).thenReturn(curvePoint);

			//WHEN
			String html = curveController.showUpdateForm(1, model, request);

			//THEN
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
	class updateCurvePointCurveControllerTests {

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
	class deleteCurvePointCurveControllerTests {

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
