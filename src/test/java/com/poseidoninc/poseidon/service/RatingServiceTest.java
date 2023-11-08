package com.poseidoninc.poseidon.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.Rating;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import com.poseidoninc.poseidon.repository.RatingRepository;

@ExtendWith(MockitoExtension.class)
public class RatingServiceTest {
	@InjectMocks
	private RatingServiceImpl ratingService;
	
	@Mock
	private RatingRepository ratingRepository;
	
	@Spy
	private final RequestService requestService = new RequestServiceImpl();
	
	private MockHttpServletRequest requestMock;
	private WebRequest request;
	private Rating rating;

	@Nested
	@Tag("getRatingByIdTests")
	@DisplayName("Tests for getting rating by ratingId")
	@TestInstance(Lifecycle.PER_CLASS)
	class GetRatingByIdTests {
		
		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/rating/getById/1");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}
		
		@AfterEach
		public void unSetForEachTests() {
			ratingService = null;
			rating = null;
		}

		@Test
		@Tag("RatingServiceTest")
		@DisplayName("test getRatingById should return expected rating")
		public void getRatingByIdTestShouldReturnExpectedRating() {
			
			//GIVEN
			rating = new Rating();
			rating.setId(1);
			rating.setMoodysRating("Moody's Rating");
			rating.setSandPRating("SandP's Rating");
			rating.setFitchRating("Fitch's Rating");
			rating.setOrderNumber(2);
			when(ratingRepository.findById(anyInt())).thenReturn(Optional.of(rating));
			
			//WHEN
			Rating ratingResult = ratingService.getRatingById(1, request);
			
			//THEN
			assertThat(ratingResult).extracting(
					Rating::getId,
					Rating::getMoodysRating,
					Rating::getSandPRating,
					Rating::getFitchRating,
					Rating::getOrderNumber)
			.containsExactly(
					1,
					"Moody's Rating",
					"SandP's Rating",
					"Fitch's Rating",
					2);
		}
				
		@Test
		@Tag("RatingServiceTest")
		@DisplayName("test getRatingById should throw UnexpectedRollbackException on InvalidDataAccessApiUsageException")
		public void getRatingByIdTestShouldThrowsUnexpectedRollbackExceptionOnInvalidDataAccessApiUsageException() {
			//GIVEN
			when(ratingRepository.findById(nullable(Integer.class))).thenThrow(new InvalidDataAccessApiUsageException("The given id must not be null"));
			
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
				() -> ratingService.getRatingById(null, request))
				.getMessage()).isEqualTo("Error while getting rating");
		}

		@Test
		@Tag("RatingServiceTest")
		@DisplayName("test getRatingById should throw UnexpectedRollbackException on ResourceNotFoundException")
		public void getRatingByIdTestShouldThrowsUnexpectedRollbackExceptionOnResourceNotFoundException() {
			//GIVEN
			when(ratingRepository.findById(anyInt())).thenReturn(Optional.empty());
			
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
				() -> ratingService.getRatingById(1, request))
				.getMessage()).isEqualTo("Error while getting rating");
		}
		
		@Test
		@Tag("RatingServiceTest")
		@DisplayName("test getRatingById should throw UnexpectedRollbackException on any RuntimeException")
		public void getRatingByIdTestShouldThrowsUnexpectedRollbackExceptionOnAnyRuntimeException() {
			//GIVEN
			when(ratingRepository.findById(anyInt())).thenThrow(new RuntimeException());
			
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
				() -> ratingService.getRatingById(1, request))
				.getMessage()).isEqualTo("Error while getting rating");
		}
	}
	
	@Nested
	@Tag("getRatingsTests")
	@DisplayName("Tests for getting ratings")
	@TestInstance(Lifecycle.PER_CLASS)
	class GetRatingsTests {
		
		private Pageable pageRequest;
		
		@BeforeAll
		public void setUpForAllTests() {
			pageRequest = Pageable.unpaged();
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/rating/getRatings");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			pageRequest = null;
			requestMock = null;
			request = null;
		}
		
		@AfterEach
		public void unSetForEachTests() {
			ratingService = null;
			rating = null;
		}

		@Test
		@Tag("RatingServiceTest")
		@DisplayName("test getRatings should return ratings")
		public void getRatingsTestShouldReturnRatings() {
			
			//GIVEN
			List<Rating> expectedRatings = new ArrayList<>();
			rating = new Rating();
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
			when(ratingRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<Rating>(expectedRatings, pageRequest, 2));
			
			//WHEN
			Page<Rating> resultedRatings = ratingService.getRatings(pageRequest, request);
			
			//THEN
			assertThat(resultedRatings).containsExactlyElementsOf(expectedRatings);
		}
		
		@Test
		@Tag("RatingServiceTest")
		@DisplayName("test getRatings should throw UnexpectedRollbackException on NullPointerException")
		public void getRatingsTestShouldThrowsUnexpectedRollbackExceptionOnNullPointerException() {
			//GIVEN
			when(ratingRepository.findAll(nullable(Pageable.class))).thenThrow(new NullPointerException());
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> ratingService.getRatings(pageRequest, request))
					.getMessage()).isEqualTo("Error while getting ratings");
		}
		
		@Test
		@Tag("RatingServiceTest")
		@DisplayName("test getRatings should throw UnexpectedRollbackException on any RuntimeException")
		public void getRatingsTestShouldThrowsUnexpectedRollbackExceptionOnAnyRuntimeException() {
			//GIVEN
			when(ratingRepository.findAll(any(Pageable.class))).thenThrow(new RuntimeException());
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> ratingService.getRatings(pageRequest, request))
					.getMessage()).isEqualTo("Error while getting ratings");
		}
	}
	
	@Nested
	@Tag("saveRatingTests")
	@DisplayName("Tests for saving rating")
	@TestInstance(Lifecycle.PER_CLASS)
	class SaveRatingTests {
		
		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/rating/saveRating/");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}
		
		@BeforeEach
		public void setUpForEachTest() {
			rating = new Rating();
			rating.setMoodysRating("Moody's Rating");
			rating.setSandPRating("SandP's Rating");
			rating.setFitchRating("Fitch's Rating");
			rating.setOrderNumber(2);
		}
		
		@AfterEach
		public void unSetForEachTests() {
			ratingService = null;
			rating = null;
		}

		@Test
		@Tag("RatingServiceTest")
		@DisplayName("test saveRating should return rating")
		public void saveRatingTestShouldReturnRating() {
			
			//GIVEN
			when(ratingRepository.save(any(Rating.class))).then(invocation -> {
				Rating ratingSaved = invocation.getArgument(0);
				ratingSaved.setId(1);
				return ratingSaved;
			});
			
			//WHEN
			Rating ratingResult = ratingService.saveRating(rating, request);
			
			//THEN
			verify(ratingRepository).save(any(Rating.class)); //times(1) is the default and can be omitted
			assertThat(ratingResult).extracting(
					Rating::getId,
					Rating::getMoodysRating,
					Rating::getSandPRating,
					Rating::getFitchRating,
					Rating::getOrderNumber)
			.containsExactly(
					1,
					"Moody's Rating",
					"SandP's Rating",
					"Fitch's Rating",
					2);

		}
				
		@Test
		@Tag("RatingServiceTest")
		@DisplayName("test saveRating should throw UnexpectedRollbackException on any RuntimeException")
		public void saveRatingTestShouldThrowUnexpectedRollbackExceptionOnAnyRuntimeException() {
			
			//GIVEN
			rating.setId(1);
			when(ratingRepository.save(any(Rating.class))).thenThrow(new RuntimeException());

			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> ratingService.saveRating(rating, request))
					.getMessage()).isEqualTo("Error while saving rating");
		}	
	}

	@Nested
	@Tag("deleteRatingTests")
	@DisplayName("Tests for deleting ratings")
	@TestInstance(Lifecycle.PER_CLASS)
	class DeleteRatingTests {
		
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
		
		@BeforeEach
		public void setUpForEachTest() {
			rating = new Rating();
			rating.setId(1);
			rating.setMoodysRating("Moody's Rating");
			rating.setSandPRating("SandP's Rating");
			rating.setFitchRating("Fitch's Rating");
			rating.setOrderNumber(2);
		}
		
		@AfterEach
		public void unSetForEachTests() {
			ratingService = null;
			rating = null;
		}

		@Test
		@Tag("RatingServiceTest")
		@DisplayName("test deleteRating by Id should delete it")
		public void deleteRatingByIdTestShouldDeleteIt() {
			
			//GIVEN
			when(ratingRepository.findById(anyInt())).thenReturn(Optional.of(rating));
			ArgumentCaptor<Rating> ratingBeingDeleted = ArgumentCaptor.forClass(Rating.class);
			doNothing().when(ratingRepository).delete(any(Rating.class));// Needed to Capture rating
			
			//WHEN
			ratingService.deleteRatingById(1, request);
			
			//THEN
			verify(ratingRepository, times(1)).delete(ratingBeingDeleted.capture());
			assertThat(ratingBeingDeleted.getValue()).extracting(
					Rating::getId,
					Rating::getMoodysRating,
					Rating::getSandPRating,
					Rating::getFitchRating,
					Rating::getOrderNumber)
			.containsExactly(
					1,
					"Moody's Rating",
					"SandP's Rating",
					"Fitch's Rating",
					2);
		}
		
		@Test
		@Tag("RatingServiceTest")
		@DisplayName("test deleteRating by Id by Id should throw  UnexpectedRollbackException on UnexpectedRollbackException")
		public void deleteRatingByIdTestShouldThrowUnexpectedRollbackExceptionOnUnexpectedRollbackException() {

			//GIVEN
			when(ratingRepository.findById(anyInt())).thenThrow(new UnexpectedRollbackException("Error while getting rating"));

			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> ratingService.deleteRatingById(2, request))
					.getMessage()).isEqualTo("Error while deleting rating");
		}	

		@Test
		@Tag("RatingServiceTest")
		@DisplayName("test deleteRating by Id should throw UnexpectedRollbackException On InvalidDataAccessApiUsageException()")
		public void deleteRatingByIdTestShouldThrowsUnexpectedRollbackExceptionOnInvalidDataAccessApiUsageException() {

			//GIVEN
			when(ratingRepository.findById(anyInt())).thenReturn(Optional.of(rating));
			doThrow(new InvalidDataAccessApiUsageException("The given id must not be null")).when(ratingRepository).delete(any(Rating.class));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> ratingService.deleteRatingById(1, request))
					.getMessage()).isEqualTo("Error while deleting rating");
		}	

		
		@Test
		@Tag("RatingServiceTest")
		@DisplayName("test deleteRating by Id should throw UnexpectedRollbackException On Any RuntimeExpceptioin")
		public void deleteRatingByIdTestShouldThrowsUnexpectedRollbackExceptionOnAnyRuntimeException() {

			//GIVEN
			when(ratingRepository.findById(anyInt())).thenReturn(Optional.of(rating));
			doThrow(new RuntimeException()).when(ratingRepository).delete(any(Rating.class));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> ratingService.deleteRatingById(1, request))
					.getMessage()).isEqualTo("Error while deleting rating");
		}	
	}

}
