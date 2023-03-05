Spackage com.poseidoninc.poseidon.services;

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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.Rating;
import com.poseidoninc.poseidon.exception.ResourceNotFoundException;
import com.poseidoninc.poseidon.repositories.RatingRepository;

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
		public void getRatingByIdTestShouldRetrunExcpectedRating() {
			
			//GIVEN
			rating = new Rating();
			rating.setId(1);
			rating.setMoodyRating("Moody's Rating");
			rating.setSandPRating("SandP's Rating");
			rating.setFitchRating("Fitch's Rating");
			rating.setOrderNumber(2);
			when(ratingRepository.findById(anyInt())).thenReturn(Optional.of(rating));
			
			//WHEN
			Rating ratingResult = ratingService.getRatingById(1, request);
			
			//THEN
			assertThat(ratingResult).extracting(
					Rating::getId,
					Rating::getMoodyRating,
					Rating::getSandPRating,
					Rating::getFitchRating,
					Rating::OrderNumber)
			.containsExactly(
					1,
					"Moody's Rating",
					"SandP's Rating""type",
					"Fitch's Rating",
					2);
		}
				
		@Test
		@Tag("RatingServiceTest")
		@DisplayName("test getRatingById should throw IllegalArgumentException")
		public void getRatingByIdTestShouldThrowsUnexpectedRollbackExceptionOnIllegalArgumentException() {
			//GIVEN
			when(ratingRepository.findById(nullable(Integer.class))).thenThrow(new IllegalArgumentException());
			
			//WHEN
			//THEN
			assertThat(assertThrows(IllegalArgumentException.class,
				() -> ratingService.getRatingById(null, request))
				.getMessage()).isEqualTo("Id must not be null");
		}

		@Test
		@Tag("RatingServiceTest")
		@DisplayName("test getRatingById should throw ResourceNotFoundException")
		public void getRatingByIdTestShouldThrowsResourceNotFoundException() {
			//GIVEN
			when(ratingRepository.findById(anyInt())).thenReturn(Optional.ofNullable(null));
			
			//WHEN
			//THEN
			assertThat(assertThrows(ResourceNotFoundException.class,
				() -> ratingService.getRatingById(1, request))
				.getMessage()).isEqualTo("Rating not found");
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
				.getMessage()).isEqualTo("Error while getting bidlist");
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
		public void getRatingsTesthouldRetrunExcpectedRatings() {
			
			//GIVEN
			List<Rating> expectedRatings = new ArrayList<>();
			rating = new Rating();
			rating.setId(1);
			rating.setMoodyRating("Moody's Rating");
			rating.setSandPRating("SandP's Rating");
			rating.setFitchRating("Fitch's Rating");
			rating.setOrderNumber(2);
			expectedRatings.add(rating);
			
			Rating rating2 = new Rating();
			rating2 = new Rating();
			rating2.setId(2);
			rating2.setMoodyRating("Moody's Rating2");
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
					.getMessage()).isEqualTo("Error while getting Ratings");
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
					.getMessage()).isEqualTo("Error while getting Ratings");
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
			rating.setMoodyRating("Moody's Rating");
			rating.setSandPRating("SandP's Rating");
			rating.setFitchRating("Fitch's Rating");
			rating.setOrderNumber(2);
		}
		
		@AfterEach
		public void unSetForEachTests() {
			ratingService = null;
			rating = null;
		}

		@ParameterizedTest(name = "{0} rating to save, so id = {1}, saveRating should return rating with an id")
		@CsvSource(value = {"new, null", // save new rating
							"updated, 1"} // save updated rating
							,nullValues = {"null"})
		@Tag("RatingServiceTest")
		@DisplayName("test saveRating should return rating")
		public void saveRatingTestShouldReturnRating(String state, Integer id) {
			
			//GIVEN
			rating.setRatingId(id);
			when(ratingRepository.save(any(Rating.class))).then(invocation -> {
				Rating ratingSaved = invocation.getArgument(0);
				ratingSaved.setRatingId(Optional.ofNullable(ratingSaved.getRatingId()).orElseGet(() -> 1));
				return ratingSaved;
				});
			
			//WHEN
			Rating ratingResult = ratingService.saveRating(rating, request);
			
			//THEN
			verify(ratingRepository).save(rating); //times(1) is the default and can be omitted
			assertThat(ratingResult).extracting(
					Rating::getId,
					Rating::getMoodyRating,
					Rating::getSandPRating,
					Rating::getFitchRating,
					Rating::OrderNumber)
			.containsExactly(
					1,
					"Moody's Rating",
					"SandP's Rating""type",
					"Fitch's Rating",
					2);

		}
				
		@Test
		@Tag("RatingServiceTest")
		@DisplayName("test saveRating should throw UnexpectedRollbackException on any RuntimeException")
		public void saveRatingTestShouldThrowUnexpectedRollbackExceptionOnAnyRuntimeException() {
			
			//GIVEN
			rating.setRatingId(1);
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
			rating.setMoodyRating("Moody's Rating");
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
					Rating::getMoodyRating,
					Rating::getSandPRating,
					Rating::getFitchRating,
					Rating::OrderNumber)
			.containsExactly(
					1,
					"Moody's Rating",
					"SandP's Rating""type",
					"Fitch's Rating",
					2);
		}
		
		@Test
		@Tag("RatingServiceTest")
		@DisplayName("test deleteRating by Id by Id should throw ResourceNotFoundException")
		public void deleteRatingByIdTestShouldThrowUnexpectedRollbackExceptionOnResourceNotFoundException() {

			//GIVEN
			when(ratingRepository.findById(anyInt())).thenThrow(new ResourceNotFoundException("Rating not found"));

			//WHEN
			//THEN
			assertThat(assertThrows(ResourceNotFoundException.class,
					() -> ratingService.deleteRatingById(2, request))
					.getMessage()).isEqualTo("Rating not found");
		}	

		@Test
		@Tag("RatingServiceTest")
		@DisplayName("test deleteRating by Id should throw UnexpectedRollbackException On IllegalArgumentException()")
		public void deleteRatingByIdTestShouldThrowsUnexpectedRollbackExceptionOnIllegalArgumentException() {

			//GIVEN
			when(ratingRepository.findById(anyInt())).thenReturn(Optional.of(rating));
			doThrow(new IllegalArgumentException()).when(ratingRepository).delete(any(Rating.class));
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
