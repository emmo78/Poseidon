package com.poseidoninc.poseidon.controller;

import com.poseidoninc.poseidon.domain.CurvePoint;
import com.poseidoninc.poseidon.service.CurvePointService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

	@AfterEach
	public void unsetForEachTest() {
		curveController = null;
	}

	@Test
	@Tag("CurveControllerTest")
	@DisplayName("test home should return \"curvePoint list\" ")
	public void homeTest() {
		
		//GIVEN
		//WHEN
		String html = curveController.home(model, request);
		
		//THEN
		assertThat(html).isEqualTo("curvePoint/list");
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
	@Test
	@Tag("CurveControllerTest")
	@DisplayName("test validate should return \"redirect:/curvePoint/list\"")
	public void validateTestShouldReturnStringRedirectCurvePointList() {

		//GIVEN
		CurvePoint curvePoint = new CurvePoint();
		when(bindingResult.hasErrors()).thenReturn(false);

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
		when(curvePointService.saveCurvePoint(any(CurvePoint.class), any(WebRequest.class))).thenThrow(new DataIntegrityViolationException(""));

		//WHEN
		String html = curveController.validate(curvePoint, bindingResult, request);

		//THEN
		assertThat(html).isEqualTo("curvePoint/add");
	}

	@Test
	@Tag("CurveControllerTest")
	@DisplayName("test showUpdateForm should return \"curvePoint/update\"")
	public void showUpdateFormTestShouldReturnStringCurvePointUpdate() {

		//GIVEN
		CurvePoint  curvePoint = new CurvePoint();

		//WHEN
		String html = curveController.showUpdateForm(1, model, request);

		//THEN
		assertThat(html).isEqualTo("curvePoint/update");
	}

	@Test
	@Tag("CurveControllerTest")
	@DisplayName("test update CurvePoint should return \"redirect:/curvePoint/list\"")
	public void updateCurveTestShouldReturnStringRedirectCurvePointList() {

		//GIVEN
		 CurvePoint curvePoint = new CurvePoint();
		when(bindingResult.hasErrors()).thenReturn(false);

		//WHEN
		String html = curveController.updateCurvePoint(curvePoint, bindingResult, request);

		//THEN
		assertThat(html).isEqualTo("redirect:/curvePoint/list");
	}

	@Test
	@Tag("CurveControllerTest")
	@DisplayName("test update CurvePoint should return \"curvePoint/update\" on BindingResultError")
	public void updateCurveTestShouldReturnStringCurvePointUpdateOnBindingResulError() {

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
	public void updateCurveTestShouldReturnStringCurvePointUpdateDataIntegrityViolationException() {

		//GIVEN
		CurvePoint curvePoint = new CurvePoint();
		when(bindingResult.hasErrors()).thenReturn(false);
		when(curvePointService.saveCurvePoint(any(CurvePoint.class), any(WebRequest.class))).thenThrow(new DataIntegrityViolationException(""));

		//WHEN
		String html = curveController.updateCurvePoint(curvePoint, bindingResult, request);

		//THEN
		assertThat(html).isEqualTo("curvePoint/update");
	}

	@Test
	@Tag("CurveControllerTest")
	@DisplayName("test delete CurvePoint should return \"redirect:/curvePoint/list\"")
	public void deleteCurveTestShouldReturnStringRedirectCurvePpointList() {

		//GIVEN
		//WHEN
		String html = curveController.deleteCurvePoint(1, request);

		//THEN
		assertThat(html).isEqualTo("redirect:/curvePoint/list");
	}
}
