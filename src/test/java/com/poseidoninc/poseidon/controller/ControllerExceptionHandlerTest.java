package com.poseidoninc.poseidon.controller;

import com.poseidoninc.poseidon.service.RequestService;
import com.poseidoninc.poseidon.service.RequestServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.ui.Model;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ControllerExceptionHandlerTest {

	@InjectMocks
	private ControllerExceptionHandler controllerExceptionHandler;

	@Mock
	private Model model;

	@Spy
	private final RequestService requestService = new RequestServiceImpl();

	private MockHttpServletRequest requestMock;
	private WebRequest request;
	private ArgumentCaptor<String> stringArgumentCaptor;

	@BeforeAll
	public void setUpForAllTests() {
		requestMock = new MockHttpServletRequest();
		requestMock.setServerName("http://localhost:8080");
		requestMock.setRequestURI("/");
		requestMock.setMethod("GET");
		request = new ServletWebRequest(requestMock);
	}

	@AfterAll
	public void unSetForAllTests() {
		requestMock = null;
		request = null;
	}
	@BeforeEach
	public void setForEachTest() {
		stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
	}

	@AfterEach
	public void unsetForEachTest() {
		controllerExceptionHandler = null;
		stringArgumentCaptor =null;
	}

	@Test
	@Tag("ControllerExceptionHandlerTest")
	@DisplayName("test unexpectedRollbackException should return \"error\" ")
	public void unexpectedRollbackExceptionTest() {
		
		//GIVEN
		UnexpectedRollbackException ure = new UnexpectedRollbackException("Error while...");
		//WHEN
		String html = controllerExceptionHandler.unexpectedRollbackException(ure, request, model);
		//THEN
		verify(model).addAttribute(stringArgumentCaptor.capture(), stringArgumentCaptor.capture());
		assertThat(stringArgumentCaptor.getAllValues())
				.containsExactly("errorMessage", "Error while...");
		assertThat(html).isEqualTo("error");
	}

	@Test
	@Tag("ControllerExceptionHandlerTest")
	@DisplayName("test unexpectedException should return \"error\" ")
	public void unexpectedExceptionTest() {

		//GIVEN
		Exception e = new Exception("Unexpected Error...");
		//WHEN
		String html = controllerExceptionHandler.unexpectedException(e, request, model);
		//THEN
		verify(model).addAttribute(stringArgumentCaptor.capture(), stringArgumentCaptor.capture());
		assertThat(stringArgumentCaptor.getAllValues())
				.containsExactly("errorMessage", "Internal Server Error");
		assertThat(html).isEqualTo("error");
	}
}
