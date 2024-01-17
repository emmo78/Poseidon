package com.poseidoninc.poseidon.controller;

import com.poseidoninc.poseidon.domain.Rating;
import com.poseidoninc.poseidon.service.RatingService;
import com.poseidoninc.poseidon.service.RequestService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

	@Mock
	private RequestService requestService;

	@AfterEach
	public void unsetForEachTest() {
		ratingService = null;
		ratingController = null;
	}

	@Nested
	@Tag("homeRatingControllerTests")
	@DisplayName("Tests for /rating/list")
	class HomeRatingControllerTests {
		@Test
		@Tag("RatingControllerTest")
		@DisplayName("test home should return \"rating/list\"")
		public void homeTestShouldReturnRatingList() {

			//GIVEN
			when(ratingService.getRatings(any(Pageable.class))).thenReturn(new PageImpl<Rating>(new ArrayList<>()));

			//WHEN
			String html = ratingController.home(model, request);

			//THEN
			assertThat(html).isEqualTo("rating/list");
		}

		@Test
		@Tag("RatingControllerTest")
		@DisplayName("test home should throw UnexpectedRollbackException")
		public void homeTestShouldThrowUnexpectedRollbackException() {

			//GIVEN
			when(ratingService.getRatings(any(Pageable.class))).thenThrow(new UnexpectedRollbackException("Error while getting ratingss"));

			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> ratingController.home(model, request))
					.getMessage()).isEqualTo("Error while getting ratings");
		}
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
	
	@Nested
	@Tag("validateRatingControllerTests")
	@DisplayName("Tests for /rating/validate")
	class ValidateRatingControllerTests {

		@Test
		@Tag("RatingControllerTest")
		@DisplayName("test validate should return \"redirect:/rating/list\"")
		public void validateTestShouldReturnStringRedirectRatingList() {

			//GIVEN
			Rating rating = new Rating();
			when(bindingResult.hasErrors()).thenReturn(false);
			when(ratingService.saveRating(any(Rating.class))).thenReturn(rating);

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
		@DisplayName("test validate should throw UnexpectedRollbackException")
		public void validateTestShouldThrowUnexpectedRollbackException() {

			//GIVEN
			Rating rating = new Rating();
			when(bindingResult.hasErrors()).thenReturn(false);
			when(ratingService.saveRating(any(Rating.class))).thenThrow(new UnexpectedRollbackException("Error while saving rating"));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> ratingController.validate(rating, bindingResult, request))
					.getMessage()).isEqualTo("Error while saving rating");
		}
	}

	@Nested
	@Tag("showUpdateFormRatingControllerTests")
	@DisplayName("Tests for /rating/update/{id}")
	class ShowUpdateFormRatingControllerTests {

		@Test
		@Tag("RatingControllerTest")
		@DisplayName("test showUpdateForm should return \"rating/update\"")
		public void showUpdateFormTestShouldReturnStringRatingUpdate() {

			//GIVEN
			Rating rating = new Rating();
			when(ratingService.getRatingById(anyInt())).thenReturn(rating);

			//WHEN
			String html = ratingController.showUpdateForm(1, model, request);

			//THEN
			assertThat(html).isEqualTo("rating/update");
		}
		@Test
		@Tag("RatingControllerTest")
		@DisplayName("test showUpdateForm should throw UnexpectedRollbackException")
		public void showUpdateFormTestShouldThrowUnexpectedRollbackException() {

			//GIVEN
			when(ratingService.getRatingById(anyInt())).thenThrow(new UnexpectedRollbackException("Error while getting rating"));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> ratingController.showUpdateForm(1, model, request))
					.getMessage()).isEqualTo("Error while getting rating");
		}
	}

	@Nested
	@Tag("updateBidRatingControllerTests")
	@DisplayName("Tests for /rating/update")
	class updateBidRatingControllerTests {

		@Test
		@Tag("RatingControllerTest")
		@DisplayName("test update Rating should return \"redirect:/rating/list\"")
		public void updateCurveTestShouldReturnStringRedirectRatingList() {

			//GIVEN
			Rating rating = new Rating();
			when(bindingResult.hasErrors()).thenReturn(false);
			when(ratingService.saveRating(any(Rating.class))).thenReturn(rating);


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
		@DisplayName("test update Bid should throw UnexpectedRollbackException")
		public void updateBidTestShouldThrowUnexpectedRollbackException() {

			//GIVEN
			Rating rating = new Rating();
			when(bindingResult.hasErrors()).thenReturn(false);
			when(ratingService.saveRating(any(Rating.class))).thenThrow(new UnexpectedRollbackException("Error while saving rating"));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> ratingController.updateRating(rating, bindingResult, request))
					.getMessage()).isEqualTo("Error while saving rating");
		}
	}

	@Nested
	@Tag("deleteBidRatingControllerTests")
	@DisplayName("Tests for /rating/delete/{id}")
	class deleteBidRatingControllerTests {
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

		@Test
		@Tag("RatingControllerTest")
		@DisplayName("test delete Bid should throw UnexpectedRollbackException")
		public void deleteBidTestShouldThrowUnexpectedRollbackException() {

			//GIVEN
			doThrow(new UnexpectedRollbackException("Error while deleting rating")).when(ratingService).deleteRatingById(anyInt());
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> ratingController.deleteRating(1, request))
					.getMessage()).isEqualTo("Error while deleting rating");
		}
	}
}
