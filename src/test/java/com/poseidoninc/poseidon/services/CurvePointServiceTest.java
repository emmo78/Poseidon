package com.poseidoninc.poseidon.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.CurvePoint;
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
				.getMessage()).isEqualTo("CurvePoint not found");
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
				.getMessage()).isEqualTo("Error while getting bidlist");
		}
	}
}
