package com.poseidoninc.poseidon.controller;

import com.poseidoninc.poseidon.domain.Rating;
import com.poseidoninc.poseidon.service.RatingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RatingControllerTest {
	

	@InjectMocks
	private RatingController ratingController;

	@Mock
	private RatingService ratingService;
	
	@Mock
	private BindingResult bindingResult;
	
	@Mock
	private Model model;

	@Mock
	private WebRequest request;

	@AfterEach
	public void unsetForEachTest() {
		ratingController = null;
	}

	@Test
	@Tag("RatingControllerTest")
	@DisplayName("test home should return \"rating/list\"")
	public void homeTest() {
		
		//GIVEN
		//WHEN
		String html = ratingController.home(model, request);
		
		//THEN
		assertThat(html).isEqualTo("rating/list");
	}

	@Test
	@Tag("RatingControllerTest")
	@DisplayName("test addRatingForm should return \"rating/add\"")
	public void ratingAddTestShouldReturnStringRatingAdd() {

		//GIVEN
		Rating rating = new Rating();

		//WHEN
		String html = ratingController.addRatingForm(rating);

		//THEN
		assertThat(html).isEqualTo("rating/add");
	}
	@Test
	@Tag("RatingControllerTest")
	@DisplayName("test validate should return \"redirect:/rating/list\"")
	public void validateTestShouldReturnStringRedirectRatingList() {

		//GIVEN
		Rating rating = new Rating();
		when(bindingResult.hasErrors()).thenReturn(false);

		//WHEN
		String html = ratingController.validate(rating, bindingResult, request);

		//THEN
		assertThat(html).isEqualTo("redirect:/rating/list");
	}

	@Test
	@Tag("RatingControllerTest")
	@DisplayName("test validate should return \"rating/add\" on BindingResultError")
	public void validateTestShouldReturnStringRatingAddOnBindingResulError() {

		//GIVEN
		Rating rating = new Rating();
		when(bindingResult.hasErrors()).thenReturn(true);

		//WHEN
		String html = ratingController.validate(rating, bindingResult, request);

		//THEN
		assertThat(html).isEqualTo("rating/add");
	}

	@Test
	@Tag("RatingControllerTest")
	@DisplayName("test showUpdateForm should return \"rating/update\"")
	public void showUpdateFormTestShouldReturnStringRatingUpdate() {

		//GIVEN
		Rating  rating = new Rating();

		//WHEN
		String html = ratingController.showUpdateForm(1, model, request);

		//THEN
		assertThat(html).isEqualTo("rating/update");
	}

	@Test
	@Tag("RatingControllerTest")
	@DisplayName("test update Rating should return \"redirect:/rating/list\"")
	public void updateCurveTestShouldReturnStringRedirectRatingList() {

		//GIVEN
		 Rating rating = new Rating();
		when(bindingResult.hasErrors()).thenReturn(false);

		//WHEN
		String html = ratingController.updateRating(rating, bindingResult, request);

		//THEN
		assertThat(html).isEqualTo("redirect:/rating/list");
	}

	@Test
	@Tag("RatingControllerTest")
	@DisplayName("test update Rating should return \"rating/update\" on BindingResultError")
	public void updateCurveTestShouldReturnStringRatingUpdateOnBindingResulError() {

		//GIVEN
		Rating rating = new Rating();
		when(bindingResult.hasErrors()).thenReturn(true);

		//WHEN
		String html = ratingController.updateRating(rating, bindingResult, request);

		//THEN
		assertThat(html).isEqualTo("rating/update");
	}

	@Test
	@Tag("RatingControllerTest")
	@DisplayName("test delete Rating should return \"redirect:/rating/list\"")
	public void deleteCurveTestShouldReturnStringRedirectCurvePointList() {

		//GIVEN
		//WHEN
		String html = ratingController.deleteRating(1, request);

		//THEN
		assertThat(html).isEqualTo("redirect:/rating/list");
	}
}
