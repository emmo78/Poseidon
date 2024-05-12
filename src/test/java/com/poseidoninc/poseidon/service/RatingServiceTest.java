package com.poseidoninc.poseidon.service;

import com.poseidoninc.poseidon.domain.Rating;
import com.poseidoninc.poseidon.repository.RatingRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.UnexpectedRollbackException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RatingServiceTest {
	@InjectMocks
	private RatingServiceImpl ratingService;
	
	@Mock
	private RatingRepository ratingRepository;
	
	private Rating rating;

	@Nested
	@Tag("getRatingByIdTests")
	@DisplayName("Tests for getting rating by ratingId")
	class GetRatingByIdTests {
		
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
			Rating ratingResult = ratingService.getRatingById(1);
			
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
				() -> ratingService.getRatingById(null))
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
				() -> ratingService.getRatingById(1))
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
				() -> ratingService.getRatingById(1))
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
		}

		@AfterAll
		public void unSetForAllTests() {
			pageRequest = null;
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
			Page<Rating> resultedRatings = ratingService.getRatings(pageRequest);
			
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
					() -> ratingService.getRatings(pageRequest))
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
					() -> ratingService.getRatings(pageRequest))
					.getMessage()).isEqualTo("Error while getting ratings");
		}
	}
	
	@Nested
	@Tag("saveRatingTests")
	@DisplayName("Tests for saving rating")
	class SaveRatingTests {

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
			Rating ratingExpected = new Rating();
			ratingExpected.setId(1);
			ratingExpected.setMoodysRating("Moody's Rating");
			ratingExpected.setSandPRating("SandP's Rating");
			ratingExpected.setFitchRating("Fitch's Rating");
			ratingExpected.setOrderNumber(2);
			when(ratingRepository.save(any(Rating.class))).thenReturn(ratingExpected);
			
			//WHEN
			Rating ratingResult = ratingService.saveRating(rating);
			
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
			when(ratingRepository.save(any(Rating.class))).thenThrow(new RuntimeException());

			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> ratingService.saveRating(rating))
					.getMessage()).isEqualTo("Error while saving rating");
		}	
	}

	@Nested
	@Tag("deleteRatingTests")
	@DisplayName("Tests for deleting ratings")
	class DeleteRatingTests {

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
			ratingService.deleteRatingById(1);
			
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
		@DisplayName("test deleteRating by Id by Id should throw UnexpectedRollbackException on UnexpectedRollbackException")
		public void deleteRatingByIdTestShouldThrowUnexpectedRollbackExceptionOnUnexpectedRollbackException() {

			//GIVEN
			when(ratingRepository.findById(anyInt())).thenThrow(new UnexpectedRollbackException("Error while getting rating"));

			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> ratingService.deleteRatingById(2))
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
					() -> ratingService.deleteRatingById(1))
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
					() -> ratingService.deleteRatingById(1))
					.getMessage()).isEqualTo("Error while deleting rating");
		}	
	}
}
