package com.poseidoninc.poseidon.service;

import com.poseidoninc.poseidon.domain.CurvePoint;
import com.poseidoninc.poseidon.repository.CurvePointRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.UnexpectedRollbackException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * unit test class for the CurvePointService.
 * @author olivier morel
 */
@ExtendWith(MockitoExtension.class)
public class CurvePointServiceTest {

	@InjectMocks
	private CurvePointServiceIpml curvePointService;
	
	@Mock
	private CurvePointRepository curvePointRepository;
	
	private CurvePoint curvePoint;
	
	@Nested
	@Tag("getCurvePointByIdTests")
	@DisplayName("Tests for getting curvePoint by curvePointId")
	class GetCurvePointByIdTests {
		
		@AfterEach
		public void unSetForEachTests() {
			curvePointService = null;
			curvePoint = null;
		}

		@Test
		@Tag("CurvePointServiceTest")
		@DisplayName("test getCurvePointById should return expected curvePoint")
		public void getCurvePointByIdTestShouldReturnExpectedCurvePoint() {
			
			//GIVEN
			curvePoint = new CurvePoint();
			curvePoint.setId(1);
			curvePoint.setCurveId(2);
			curvePoint.setAsOfDate(LocalDateTime.parse("21/01/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			curvePoint.setTerm(3.0);
			curvePoint.setValue(4.0);
			curvePoint.setCreationDate(LocalDateTime.parse("22/01/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			when(curvePointRepository.findById(anyInt())).thenReturn(Optional.of(curvePoint));
			
			//WHEN
			CurvePoint curvePointResult = curvePointService.getCurvePointById(1);
			
			//THEN
			assertThat(curvePointResult).extracting(
					CurvePoint::getId,
					CurvePoint::getCurveId,
					curve -> curve.getAsOfDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
					CurvePoint::getTerm,
					CurvePoint::getValue,
					curve -> curve.getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
			.containsExactly(
					1,
					2,
					"21/01/2023 10:20:30",
					3.0,
					4.0,
					"22/01/2023 12:22:32"
					);
		}
				
		@Test
		@Tag("CurvePointServiceTest")
		@DisplayName("test getCurvePointById should throw UnexpectedRollbackException On InvalidDataAccessApiUsageException")
		public void getCurvePointByIdTestShouldThrowsUnexpectedRollbackExceptionOnInvalidDataAccessApiUsageException() {
			//GIVEN
			when(curvePointRepository.findById(nullable(Integer.class))).thenThrow(new InvalidDataAccessApiUsageException("The given id must not be null"));
			
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
				() -> curvePointService.getCurvePointById(null))
				.getMessage()).isEqualTo("Error while getting curvePoint");
		}

		@Test
		@Tag("CurvePointServiceTest")
		@DisplayName("test getCurvePointById should throw UnexpectedRollbackException on ResourceNotFoundException")
		public void getCurvePointByIdTestShouldThrowsUnexpectedRollbackExceptionOnResourceNotFoundException() {
			//GIVEN
			when(curvePointRepository.findById(anyInt())).thenReturn(Optional.empty());
			
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
				() -> curvePointService.getCurvePointById(1))
				.getMessage()).isEqualTo("Error while getting curvePoint");
		}
		
		@Test
		@Tag("CurvePointServiceTest")
		@DisplayName("test getCurvePointById should throw UnexpectedRollbackException on any RuntimeException")
		public void getCurvePointByIdTestShouldThrowsUnexpectedRollbackExceptionOnAnyRuntimeException() {
			//GIVEN
			when(curvePointRepository.findById(anyInt())).thenThrow(new RuntimeException());
			
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
				() -> curvePointService.getCurvePointById(1))
				.getMessage()).isEqualTo("Error while getting curvePoint");
		}
	}
	
	@Nested
	@Tag("getCurvePointsTests")
	@DisplayName("Tests for getting curvePoints")
	@TestInstance(Lifecycle.PER_CLASS)
	class GetCurvePointsTests {
		
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
			curvePointService = null;
			curvePoint = null;
		}

		@Test
		@Tag("CurvePointServiceTest")
		@DisplayName("test getCurvePoints should return curvePoints")
		public void getCurvePointsTestShouldReturnCurvePoints() {
			
			//GIVEN
			List<CurvePoint> expectedCurvePoints = new ArrayList<>();
			curvePoint = new CurvePoint();
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
			when(curvePointRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<CurvePoint>(expectedCurvePoints, pageRequest, 2));
			
			//WHEN
			Page<CurvePoint> resultedCurvePoints = curvePointService.getCurvePoints(pageRequest);
			
			//THEN
			assertThat(resultedCurvePoints).containsExactlyElementsOf(expectedCurvePoints);
		}
		
		@Test
		@Tag("CurvePointServiceTest")
		@DisplayName("test getCurvePoints should throw UnexpectedRollbackException on NullPointerException")
		public void getCurvePointsTestShouldThrowsUnexpectedRollbackExceptionOnNullPointerException() {
			//GIVEN
			when(curvePointRepository.findAll(any(Pageable.class))).thenThrow(new NullPointerException());
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> curvePointService.getCurvePoints(pageRequest))
					.getMessage()).isEqualTo("Error while getting curvePoints");
		}
		
		@Test
		@Tag("CurvePointServiceTest")
		@DisplayName("test getCurvePoints should throw UnexpectedRollbackException on any RuntimeException")
		public void getCurvePointsTestShouldThrowsUnexpectedRollbackExceptionOnAnyRuntimeException() {
			//GIVEN
			when(curvePointRepository.findAll(any(Pageable.class))).thenThrow(new RuntimeException());
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> curvePointService.getCurvePoints(pageRequest))
					.getMessage()).isEqualTo("Error while getting curvePoints");
		}
	}
	
	@Nested
	@Tag("saveCurvePointTests")
	@DisplayName("Tests for saving curvePoints")
	class SaveCurvePointTests {
		
		@BeforeEach
		public void setUpForEachTest() {
			curvePoint = new CurvePoint();
			curvePoint.setCurveId(2);
			curvePoint.setAsOfDate(LocalDateTime.parse("21/01/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			curvePoint.setTerm(3.0);
			curvePoint.setValue(4.0);
			curvePoint.setCreationDate(LocalDateTime.parse("22/01/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
		}
		
		@AfterEach
		public void unSetForEachTests() {
			curvePointService = null;
			curvePoint = null;
		}

		@Test
		@Tag("CurvePointServiceTest")
		@DisplayName("test saveCurvePoint should persist and return curvePoint")
		public void saveCurvePointTestShouldPersistAndReturnCurvePoint() {
			
			//GIVEN
			CurvePoint curvePointExpected = new CurvePoint();
			curvePointExpected.setId(1);
			curvePointExpected.setCurveId(2);
			curvePointExpected.setAsOfDate(LocalDateTime.parse("21/01/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			curvePointExpected.setTerm(3.0);
			curvePointExpected.setValue(4.0);
			curvePointExpected.setCreationDate(LocalDateTime.parse("22/01/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			when(curvePointRepository.save(any(CurvePoint.class))).thenReturn(curvePointExpected);
			
			//WHEN
			CurvePoint resultedCurvePoint = curvePointService.saveCurvePoint(curvePoint);
			
			//THEN
			verify(curvePointRepository).save(any(CurvePoint.class)); //times(1) is the default and can be omitted
			assertThat(resultedCurvePoint).extracting(
					CurvePoint::getId,
					CurvePoint::getCurveId,
					curve -> curve.getAsOfDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
					CurvePoint::getTerm,
					CurvePoint::getValue,
					curve -> curve.getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
				.containsExactly(
					1,	
					2,
					"21/01/2023 10:20:30",
					3.0,
					4.0,
					"22/01/2023 12:22:32");
		}
		
		@Test
		@Tag("CurvePointServiceTest")
		@DisplayName("test saveCurvePoint should throw DataIntegrityViolationException")
		public void saveCurvePointTestShouldThrowsDataIntegrityViolationException() {

			//GIVEN
			when(curvePointRepository.save(any(CurvePoint.class))).thenThrow(new DataIntegrityViolationException("Unique index or primary key violation"));

			//WHEN
			//THEN
			assertThat(assertThrows(DataIntegrityViolationException.class,
					() -> curvePointService.saveCurvePoint(curvePoint))
					.getMessage()).isEqualTo("CurveId already exists");
		}
		
		@Test
		@Tag("CurvePointServiceTest")
		@DisplayName("test saveCurvePoint should throw UnexpectedRollbackException on any RuntimeException")
		public void saveCurvePointTestShouldThrowUnexpectedRollbackExceptionOnAnyRuntimeException() {
			
			//GIVEN
			when(curvePointRepository.save(any(CurvePoint.class))).thenThrow(new RuntimeException());

			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> curvePointService.saveCurvePoint(curvePoint))
					.getMessage()).isEqualTo("Error while saving curvePoint");
		}	
	}
	
	@Nested
	@Tag("deleteCurvePointTests")
	@DisplayName("Tests for deleting curve point")
	class DeleteCurvePointTests {
		
		@BeforeEach
		public void setUpForEachTest() {
			curvePoint = new CurvePoint();
			curvePoint.setId(1);
			curvePoint.setCurveId(2);
			curvePoint.setAsOfDate(LocalDateTime.parse("21/01/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			curvePoint.setTerm(3.0);
			curvePoint.setValue(4.0);
			curvePoint.setCreationDate(LocalDateTime.parse("22/01/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
		}
		
		@AfterEach
		public void unSetForEachTests() {
			curvePointService = null;
			curvePoint = null;
		}

		@Test
		@Tag("CurvePointServiceTest")
		@DisplayName("test deleteCurvePoint by Id should delete it")
		public void deleteCurvePointByIdTestShouldDeleteIt() {
			
			//GIVEN
			when(curvePointRepository.findById(anyInt())).thenReturn(Optional.of(curvePoint));
			ArgumentCaptor<CurvePoint> curvePointBeingDeleted = ArgumentCaptor.forClass(CurvePoint.class);
			doNothing().when(curvePointRepository).delete(any(CurvePoint.class));// Needed to Capture curvePoint
			
			//WHEN
			curvePointService.deleteCurvePointById(1);
			
			//THEN
			verify(curvePointRepository, times(1)).delete(curvePointBeingDeleted.capture());
			assertThat(curvePointBeingDeleted.getValue()).extracting(
					CurvePoint::getId,
					CurvePoint::getCurveId,
					curve -> curve.getAsOfDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
					CurvePoint::getTerm,
					CurvePoint::getValue,
					curve -> curve.getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
			.containsExactly(
					1,
					2,
					"21/01/2023 10:20:30",
					3.0,
					4.0,
					"22/01/2023 12:22:32");	
		}
		
		@Test
		@Tag("CurvePointServiceTest")
		@DisplayName("test deleteCurvePoint by Id should throw UnexpectedRollbackException nn UnexpectedRollbackException")
		public void deleteCurvePointByIdTestShouldThrowUnexpectedRollbackExceptionOnUnexpectedRollbackException() {

			//GIVEN
			when(curvePointRepository.findById(anyInt())).thenThrow(new UnexpectedRollbackException("Error while getting curvePoints"));

			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> curvePointService.deleteCurvePointById(2))
					.getMessage()).isEqualTo("Error while deleting curvePoint");
		}	

		@Test
		@Tag("CurvePointServiceTest")
		@DisplayName("test deleteCurvePoint by Id should throw UnexpectedRollbackException On InvalidDataAccessApiUsageException")
		public void deleteCurvePointByIdTestShouldThrowsUnexpectedRollbackExceptionOnInvalidDataAccessApiUsageException() {

			//GIVEN
			when(curvePointRepository.findById(anyInt())).thenReturn(Optional.of(curvePoint));
			doThrow(new InvalidDataAccessApiUsageException("Entity must not be null")).when(curvePointRepository).delete(any(CurvePoint.class));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> curvePointService.deleteCurvePointById(1))
					.getMessage()).isEqualTo("Error while deleting curvePoint");
		}	
		
		
		@Test
		@Tag("CurvePointServiceTest")
		@DisplayName("test deleteCurvePoint by Id should throw UnexpectedRollbackException On Any RuntimeExpceptioin")
		public void deleteCurvePointByIdTestShouldThrowsUnexpectedRollbackExceptionOnAnyRuntimeException() {

			//GIVEN
			when(curvePointRepository.findById(anyInt())).thenReturn(Optional.of(curvePoint));
			doThrow(new RuntimeException()).when(curvePointRepository).delete(any(CurvePoint.class));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> curvePointService.deleteCurvePointById(1))
					.getMessage()).isEqualTo("Error while deleting curvePoint");
		}	
	}
}
