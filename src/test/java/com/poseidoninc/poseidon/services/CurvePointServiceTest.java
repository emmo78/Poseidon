package com.poseidoninc.poseidon.services;

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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.CurvePoint;
import com.poseidoninc.poseidon.exception.ResourceConflictException;
import com.poseidoninc.poseidon.exception.ResourceNotFoundException;
import com.poseidoninc.poseidon.repositories.CurvePointRepository;

@ExtendWith(MockitoExtension.class)
public class CurvePointServiceTest {
	@InjectMocks
	private CurvePointServiceIpml curvePointService;
	
	@Mock
	private CurvePointRepository curvePointRepository;
	
	@Spy
	private final RequestService requestService = new RequestServiceImpl();
	
	private MockHttpServletRequest requestMock;
	private WebRequest request;
	private CurvePoint curvePoint;
	
	@Nested
	@Tag("getCurvePointByIdTests")
	@DisplayName("Tests for getting curvePoint by curvePointId")
	@TestInstance(Lifecycle.PER_CLASS)
	class GetCurvePointByIdTests {
		
		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/curvePoint/getById/1");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}
		
		@AfterEach
		public void unSetForEachTests() {
			curvePointService = null;
			curvePoint = null;
		}

		@Test
		@Tag("CurvePointServiceTest")
		@DisplayName("test getCurvePointById should return expected curvePoint")
		public void getCurvePointByIdTestShouldRetrunExcpectedCurvePoint() {
			
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
			CurvePoint curvePointResult = curvePointService.getCurvePointById(1, request);
			
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
		@DisplayName("test getCurvePointById should throw IllegalArgumentException")
		public void getCurvePointByIdTestShouldThrowsUnexpectedRollbackExceptionOnIllegalArgumentException() {
			//GIVEN
			when(curvePointRepository.findById(nullable(Integer.class))).thenThrow(new IllegalArgumentException());
			
			//WHEN
			//THEN
			assertThat(assertThrows(IllegalArgumentException.class,
				() -> curvePointService.getCurvePointById(null, request))
				.getMessage()).isEqualTo("Id must not be null");
		}

		@Test
		@Tag("CurvePointServiceTest")
		@DisplayName("test getCurvePointById should throw ResourceNotFoundException")
		public void getCurvePointByIdTestShouldThrowsResourceNotFoundException() {
			//GIVEN
			when(curvePointRepository.findById(anyInt())).thenReturn(Optional.ofNullable(null));
			
			//WHEN
			//THEN
			assertThat(assertThrows(ResourceNotFoundException.class,
				() -> curvePointService.getCurvePointById(1, request))
				.getMessage()).isEqualTo("Curve Point not found");
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
				() -> curvePointService.getCurvePointById(1, request))
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
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/curvePoint/getCurvePoints");
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
			curvePointService = null;
			curvePoint = null;
		}

		@Test
		@Tag("CurvePointServiceTest")
		@DisplayName("test getCurvePoints should return curvePoints")
		public void getCurvePointsTesthouldRetrunExcpectedCurvePoints() {
			
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
			Page<CurvePoint> resultedCurvePoints = curvePointService.getCurvePoints(pageRequest, request);
			
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
					() -> curvePointService.getCurvePoints(pageRequest, request))
					.getMessage()).isEqualTo("Error while getting CurvePoints");
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
					() -> curvePointService.getCurvePoints(pageRequest, request))
					.getMessage()).isEqualTo("Error while getting CurvePoints");
		}
	}
	
	@Nested
	@Tag("saveCurvePointTests")
	@DisplayName("Tests for saving curvePoints")
	@TestInstance(Lifecycle.PER_CLASS)
	class SaveCurvePointTests {
		
		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/curvePoint/saveCurvePoint/");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}
		
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
			ArgumentCaptor<CurvePoint> curvePointBeingSaved = ArgumentCaptor.forClass(CurvePoint.class);
			when(curvePointRepository.save(any(CurvePoint.class))).then(invocation -> {
				CurvePoint curvePointSaved = invocation.getArgument(0);
				curvePointSaved.setId(1);
				return curvePointSaved;
				});
			
			//WHEN
			CurvePoint resultedCurvePoint = curvePointService.saveCurvePoint(curvePoint, request);
			
			//THEN
			verify(curvePointRepository, times(1)).save(curvePointBeingSaved.capture());
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
					() -> curvePointService.saveCurvePoint(curvePoint, request))
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
					() -> curvePointService.saveCurvePoint(curvePoint, request))
					.getMessage()).isEqualTo("Error while saving curvePoint");
		}	
	}
	
	@Nested
	@Tag("deleteCurvePointTests")
	@DisplayName("Tests for deleting curve point")
	@TestInstance(Lifecycle.PER_CLASS)
	class DeleteCurvePointTests {
		
		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/curvePoint/delete/1");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}
		
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
			curvePointService.deleteCurvePointById(1, request);
			
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
		@DisplayName("test deleteCurvePoint by Id by Id should throw ResourceNotFoundException")
		public void deleteCurvePointByIdTestShouldThrowUnexpectedRollbackExceptionOnResourceNotFoundException() {

			//GIVEN
			when(curvePointRepository.findById(anyInt())).thenThrow(new ResourceNotFoundException("CurvePoint not found"));

			//WHEN
			//THEN
			assertThat(assertThrows(ResourceNotFoundException.class,
					() -> curvePointService.deleteCurvePointById(2, request))
					.getMessage()).isEqualTo("CurvePoint not found");
		}	

		@Test
		@Tag("CurvePointServiceTest")
		@DisplayName("test deleteCurvePoint by Id should throw UnexpectedRollbackException On IllegalArgumentException")
		public void deleteCurvePointByIdTestShouldThrowsUnexpectedRollbackExceptionOnIllegalArgumentException() {

			//GIVEN
			when(curvePointRepository.findById(anyInt())).thenReturn(Optional.of(curvePoint));
			doThrow(new IllegalArgumentException()).when(curvePointRepository).delete(any(CurvePoint.class));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> curvePointService.deleteCurvePointById(1, request))
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
					() -> curvePointService.deleteCurvePointById(1, request))
					.getMessage()).isEqualTo("Error while deleting curvePoint");
		}	
	}

}
