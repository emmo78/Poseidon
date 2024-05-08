package com.poseidoninc.poseidon.controller.api;

import com.poseidoninc.poseidon.domain.Rating;
import com.poseidoninc.poseidon.exception.BadRequestException;
import com.poseidoninc.poseidon.service.RatingService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApiRatingControllerTest {

    @InjectMocks
    private ApiRatingController apiRatingController;

    @Mock
    private RatingService ratingService;

    @Spy
    private final RequestService requestService = new RequestServiceImpl();

    private MockHttpServletRequest requestMock;
    private WebRequest request;
    private Rating rating;

    @AfterEach
    public void unsetForEachTest() {
        ratingService = null;
        apiRatingController = null;
        rating = null;
    }

    @Nested
    @Tag("getRatings")
    @DisplayName("Tests for /api/rating/list")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GetRatingsTests {

        private Pageable pageRequest;

        @BeforeAll
        public void setUpForAllTests() {
            pageRequest = Pageable.unpaged();
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("GET");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/rating/list");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            pageRequest = null;
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("ApiRatingControllerTest")
        @DisplayName("test getRatings should return a Success ResponseEntity With Iterable Of Rating")
        public void getRatingsTestShouldReturnSuccessResponseEntityWithIterableOfRating() {

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

            when(ratingService.getRatings(any(Pageable.class))).thenReturn(new PageImpl<Rating>(expectedRatings, pageRequest, 2));

            //WHEN
            ResponseEntity<Iterable<Rating>> responseEntity = apiRatingController.getRatings(request);

            //THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            Iterable<Rating> resultRatings = responseEntity.getBody();
            assertThat(resultRatings).isNotNull();
            assertThat(resultRatings).containsExactlyElementsOf(expectedRatings);
        }

        @Test
        @Tag("ApiRatingControllerTest")
        @DisplayName("test getRatings should throw UnexpectedRollbackException")
        public void getRatingsTestShouldThrowUnexpectedRollbackException() {

            //GIVEN
            when(ratingService.getRatings(any(Pageable.class))).thenThrow(new UnexpectedRollbackException("Error while getting ratings"));

            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiRatingController.getRatings(request))
                    .getMessage()).isEqualTo("Error while getting ratings");
        }
    }

    @Nested
    @Tag("createRating")
    @DisplayName("Tests for /api/rating/create")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class CreateRatingTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("POST");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/rating/create");
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

        @Test
        @Tag("ApiRatingControllerTest")
        @DisplayName("test createRating should return a Success ResponseEntity With Saved Rating")
        public void createRatingTestShouldReturnASuccessResponseEntityWithSavedRating() {

            //GIVEN
            Optional<Rating> optionalRating = Optional.of(rating);
            Rating ratingExpected = new Rating();
            ratingExpected.setId(1);
            ratingExpected.setMoodysRating("Moody's Rating");
            ratingExpected.setSandPRating("SandP's Rating");
            ratingExpected.setFitchRating("Fitch's Rating");
            ratingExpected.setOrderNumber(2);
            when(ratingService.saveRating(any(Rating.class))).thenReturn(ratingExpected);

            //WHEN
            ResponseEntity<Rating> responseEntity = null;
            try {
                responseEntity = apiRatingController.createRating(optionalRating, request);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            Rating resultRating = responseEntity.getBody();
            assertThat(resultRating).isNotNull();
            assertThat(resultRating)
                    .extracting(
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
                            2
                    );
        }

        @Test
        @Tag("ApiRatingControllerTest")
        @DisplayName("test createRating should throw BadRequestException")
        public void createRatingTestShouldThrowBadRequestException() {

            //GIVEN
            Optional<Rating> optionalRating = Optional.empty();

            //WHEN
            //THEN
            assertThat(assertThrows(BadRequestException.class,
                    () -> apiRatingController.createRating(optionalRating, request))
                    .getMessage()).isEqualTo("Correct request should be a json rating body");
        }

        @Test
        @Tag("ApiRatingControllerTest")
        @DisplayName("test createRating should throw UnexpectedRollbackException")
        public void createRatingTestShouldThrowMUnexpectedRollbackException() {

            //GIVEN
            Optional<Rating> optionalRating = Optional.of(rating);
            when(ratingService.saveRating(any(Rating.class))).thenThrow(new UnexpectedRollbackException("Error while saving rating"));

            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiRatingController.createRating(optionalRating, request))
                    .getMessage()).isEqualTo("Error while saving rating");
        }
    }

    @Nested
    @Tag("getRatingById")
    @DisplayName("Tests for /api/rating/update/{id}")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class getRatingByIdTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("GET");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/rating/update/1");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("ApiRatingControllerTest")
        @DisplayName("test getRatingById should return a Success ResponseEntity With Rating")
        public void getRatingByIdTestShouldReturnASuccessResponseEntityWithRating() {

            //GIVEN
            rating = new Rating();
            rating.setId(1);
            rating.setMoodysRating("Moody's Rating");
            rating.setSandPRating("SandP's Rating");
            rating.setFitchRating("Fitch's Rating");
            rating.setOrderNumber(2);

            when(ratingService.getRatingById(anyInt())).thenReturn(rating);

            //WHEN
            ResponseEntity<Rating> responseEntity = apiRatingController.getRatingById(1, request);

            //THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            Rating resultRating = responseEntity.getBody();
            assertThat(resultRating).isNotNull();
            assertThat(resultRating)
                    .extracting(
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
                            2
                    );
        }

        @Test
        @Tag("ApiRatingControllerTest")
        @DisplayName("test getRatingById should throw UnexpectedRollbackException")
        public void getRatingByIdTestShouldThrowUnexpectedRollbackException() {

            //GIVEN
            when(ratingService.getRatingById(anyInt())).thenThrow(new UnexpectedRollbackException("Error while getting rating"));
            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiRatingController.getRatingById(1, request))
                    .getMessage()).isEqualTo("Error while getting rating");
        }
    }

    @Nested
    @Tag("updateRating")
    @DisplayName("Tests for /api/rating/update")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class UpdateRatingTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("PUT");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/rating/update");
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

        @Test
        @Tag("ApiRatingControllerTest")
        @DisplayName("test updateRating should return a Success ResponseEntity With Saved rating")
        public void updateRatingTestShouldReturnASuccessResponseEntityWithSavedRating() {

            //GIVEN
            Optional<Rating> optionalRating = Optional.of(rating);
            Rating ratingExpected = new Rating();
            ratingExpected.setId(1);
            ratingExpected.setMoodysRating("Moody's Rating");
            ratingExpected.setSandPRating("SandP's Rating");
            ratingExpected.setFitchRating("Fitch's Rating");
            ratingExpected.setOrderNumber(2);
            when(ratingService.saveRating(any(Rating.class))).thenReturn(ratingExpected);

            //WHEN
            ResponseEntity<Rating> responseEntity = null;
            try {
                responseEntity = apiRatingController.updateRating(optionalRating, request);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            Rating resultRating = responseEntity.getBody();
            assertThat(resultRating).isNotNull();
            assertThat(resultRating)
                    .extracting(
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
                            2
                    );
        }

        @Test
        @Tag("ApiRatingControllerTest")
        @DisplayName("test updateRating should throw BadRequestException")
        public void updateRatingTestShouldThrowBadRequestException() {

            //GIVEN
            Optional<Rating> optionalRating = Optional.empty();

            //WHEN
            //THEN
            assertThat(assertThrows(BadRequestException.class,
                    () -> apiRatingController.updateRating(optionalRating, request))
                    .getMessage()).isEqualTo("Correct request should be a json rating body");
        }

        @Test
        @Tag("ApiRatingControllerTest")
        @DisplayName("test updateRating should throw UnexpectedRollbackException")
        public void updateRatingTestShouldThrowMUnexpectedRollbackException() {

            //GIVEN
            Optional<Rating> optionalRating = Optional.of(rating);
            when(ratingService.saveRating(any(Rating.class))).thenThrow(new UnexpectedRollbackException("Error while saving rating"));

            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiRatingController.updateRating(optionalRating, request))
                    .getMessage()).isEqualTo("Error while saving rating");
        }
    }

    @Nested
    @Tag("deleteByIdTests")
    @DisplayName("Tests for /api/rating/delete/{id}")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class DeleteByIdTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("DELETE");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/rating/delete/1");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("ApiRatingControllerTest")
        @DisplayName("test deleteById should return HttpStatus.OK")
        public void deleteByIdTestShouldReturnHttpStatusOK() {

            //GIVEN
            //WHEN
            HttpStatus httpStatus = apiRatingController.deleteRatingById(1, request);

            //THEN
            assertThat(httpStatus.is2xxSuccessful()).isTrue();
        }

        @Test
        @Tag("ApiRatingControllerTest")
        @DisplayName("test deleteById should throw UnexpectedRollbackException")
        public void deleteByIdTestShouldThrowUnexpectedRollbackException() {

            //GIVEN
            doThrow(new UnexpectedRollbackException("Error while deleting rating")).when(ratingService).deleteRatingById(anyInt());
            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiRatingController.deleteRatingById(1, request))
                    .getMessage()).isEqualTo("Error while deleting rating");
        }
    }
}
