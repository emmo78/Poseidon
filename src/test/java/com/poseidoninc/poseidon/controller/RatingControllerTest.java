package com.poseidoninc.poseidon.controller;

import com.poseidoninc.poseidon.domain.BidList;
import com.poseidoninc.poseidon.domain.Rating;
import com.poseidoninc.poseidon.service.RatingService;
import com.poseidoninc.poseidon.service.RequestService;
import com.poseidoninc.poseidon.service.RequestServiceImpl;
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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * unit test class for the RatingController.
 * @author olivier morel
 */
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

	@Spy
	private final RequestService requestService = new RequestServiceImpl();

	private MockHttpServletRequest requestMock;
	private WebRequest request;

	@AfterEach
	public void unsetForEachTest() {
		ratingService = null;
		ratingController = null;
	}

	@Nested
	@Tag("homeRatingControllerTests")
	@DisplayName("Tests for /rating/list")
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class HomeRatingControllerTests {

		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/rating/list");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}
		@Test
		@Tag("RatingControllerTest")
		@DisplayName("test home should return \"rating/list\"")
		public void homeTestShouldReturnStringRatingList() {

			//GIVEN
			List<Rating> expectedRatings = new ArrayList<>();
			Rating rating = new Rating();
			rating.setId(1);
			rating.setMoodysRating("Moody's Rating");
			rating.setSandPRating("SandP's Rating");
			rating.setFitchRating("Fitch's Rating");
			rating.setOrderNumber(2);
			expectedRatings.add(rating);

			Rating rating2 = new Rating();
			rating2.setId(2);
			rating2.setMoodysRating("Moody's Rating2");
			rating2.setSandPRating("SandP's Rating2");
			rating2.setFitchRating("Fitch's Rating2");
			rating2.setOrderNumber(3);
			expectedRatings.add(rating2);

			ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
			ArgumentCaptor<Iterable<Rating>> iterableArgumentCaptor = ArgumentCaptor.forClass(Iterable.class);
			when(ratingService.getRatings(any(Pageable.class))).thenReturn(new PageImpl<Rating>(expectedRatings));

			//WHEN
			String html = ratingController.home(model, request);

			//THEN
			verify(model).addAttribute(stringArgumentCaptor.capture(), iterableArgumentCaptor.capture()); //times(1) is used by default
			assertThat(stringArgumentCaptor.getValue()).isEqualTo("ratings");
			assertThat(iterableArgumentCaptor.getValue()).containsExactlyElementsOf(expectedRatings);
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
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class ValidateRatingControllerTests {

		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/rating/validate/");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}
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
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class ShowUpdateFormRatingControllerTests {

		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/rating/update/1");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}

		@Test
		@Tag("RatingControllerTest")
		@DisplayName("test showUpdateForm should return \"rating/update\"")
		public void showUpdateFormTestShouldReturnStringRatingUpdate() {

			//GIVEN
			Rating rating = new Rating();
			rating.setId(1);
			rating.setMoodysRating("Moody's Rating");
			rating.setSandPRating("SandP's Rating");
			rating.setFitchRating("Fitch's Rating");
			rating.setOrderNumber(2);
			ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
			ArgumentCaptor<Rating> ratingArgumentCaptor = ArgumentCaptor.forClass(Rating.class);
			when(ratingService.getRatingById(anyInt())).thenReturn(rating);

			//WHEN
			String html = ratingController.showUpdateForm(1, model, request);

			//THEN
			verify(model).addAttribute(stringArgumentCaptor.capture(), ratingArgumentCaptor.capture()); //times(1) is used by default
			assertThat(stringArgumentCaptor.getValue()).isEqualTo("rating");
			assertThat(ratingArgumentCaptor.getValue()).isEqualTo(rating);
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
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class updateBidRatingControllerTests {

		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/rating/update/");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}

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
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class deleteBidRatingControllerTests {

		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/rating/delete/1");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}

		@Test
		@Tag("RatingControllerTest")
		@DisplayName("test delete Rating should return \"redirect:/rating/list\"")
		public void deleteCurveTestShouldReturnStringRedirectRatingList() {

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
