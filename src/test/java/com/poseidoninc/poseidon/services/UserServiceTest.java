package com.poseidoninc.poseidon.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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

import com.poseidoninc.poseidon.domain.User;
import com.poseidoninc.poseidon.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@InjectMocks
	private UserServiceImpl userService;
	
	@Mock
	private UserRepository userRepository;
	
	@Spy
	private RequestService requestService = new RequestServiceImpl();
	
	private MockHttpServletRequest requestMock;
	private WebRequest request;
	private User user;

	
	@Nested
	@Tag("getUserByUserNameTests")
	@DisplayName("Tests for method getUserByUserName")
	@TestInstance(Lifecycle.PER_CLASS)
	class getUserByUserNameTests {
		
		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/user/getByName/Aaa");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}
		
		@AfterEach
		public void unSetForEachTests() {
			userService = null;
			user = null;
		}

		@Test
		@Tag("UserServiceTest")
		@DisplayName("test getUserByUserName should return expected user")
		public void getUserByUserNameShouldRetrunExcpectedUser() {
			
			//GIVEN
			user = new User();
			user.setId(1);
			user.setUsername("Aaa");
			user.setPassword("aaa1=Passwd");
			user.setFullname("AAA");
			user.setRole("USER");
			when(userRepository.findByUsername(anyString())).thenReturn(user);
			
			//WHEN
			User userResult = userService.getUserByUserName("Aaa", request);
			
			//THEN
			assertThat(userResult).extracting(
					User::getId,
					User::getUsername,
					User::getPassword,
					User::getFullname,
					User::getRole)
				.containsExactly(
					1,
					"Aaa",
					"aaa1=Passwd",
					"AAA",
					"USER");	
		}
			
		@Test
		@Tag("UserServiceTest")
		@DisplayName("test getUserByUserName should throw UnexpectedRollbackException on ResourceNotFoundException")
		public void getUserByUserNameTestShouldThrowsResourceNotFoundException() {
			//GIVEN
			when(userRepository.findByUsername(anyString())).thenReturn(null);
			
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
				() -> userService.getUserByUserName("aaa@aaa.com", request))
				.getMessage()).isEqualTo("Error while getting user profile");
		}
		
		@Test
		@Tag("UserServiceTest")
		@DisplayName("test getUserByUserName should throw UnexpectedRollbackException on IllegalArgumentException")
		public void getUserByUserNameTestShouldThrowsUnexpectedRollbackExceptionOnIllegalArgumentException() {
			//GIVEN
			when(userRepository.findByUsername(anyString())).thenThrow(new IllegalArgumentException());
			
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
				() -> userService.getUserByUserName("aaa@aaa.com", request))
				.getMessage()).isEqualTo("Error while getting user profile");
		}

		@Test
		@Tag("UserServiceTest")
		@DisplayName("test getUserByUserName should throw UnexpectedRollbackException on any RuntimeException")
		public void getUserByUserNameTestShouldThrowsUnexpectedRollbackExceptionOnAnyRuntimeException() {
			//GIVEN
			when(userRepository.findByUsername(anyString())).thenThrow(new RuntimeException());
			
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
				() -> userService.getUserByUserName("aaa@aaa.com", request))
				.getMessage()).isEqualTo("Error while getting user profile");
		}
	}

	@Nested
	@Tag("getUserByIdTests")
	@DisplayName("Tests for getting user by Id")
	@TestInstance(Lifecycle.PER_CLASS)
	class getUserByIdTests {
		
		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/user/getByName/Aaa");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}
		
		@AfterEach
		public void unSetForEachTests() {
			userService = null;
			user = null;
		}

		@Test
		@Tag("UserServiceTest")
		@DisplayName("test getUserById should return expected user")
		public void getUserByIdShouldRetrunExcpectedUser() {
			
			//GIVEN
			user = new User();
			user.setId(1);
			user.setUsername("Aaa");
			user.setPassword("aaa1=Passwd");
			user.setFullname("AAA");
			user.setRole("USER");
			when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
			
			//WHEN
			User userResult = userService.getUserById(1, request);
			
			//THEN
			assertThat(userResult).extracting(
					User::getId,
					User::getUsername,
					User::getPassword,
					User::getFullname,
					User::getRole)
				.containsExactly(
					1,
					"Aaa",
					"aaa1=Passwd",
					"AAA",
					"USER");	
		}
		
		@Test
		@Tag("UserServiceTest")
		@DisplayName("test getUserById should throw UnexpectedRollbackException on ResourceNotFoundException")
		public void getUserByIdTestShouldThrowsResourceNotFoundException() {
			//GIVEN
			when(userRepository.findById(anyInt())).thenReturn(null);
			
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
				() -> userService.getUserById(1, request))
				.getMessage()).isEqualTo("Error while getting user profile");
		}
		
		@Test
		@Tag("UserServiceTest")
		@DisplayName("test getUserById should throw UnexpectedRollbackException on IllegalArgumentException")
		public void getUserByIdTestShouldThrowsUnexpectedRollbackExceptionOnIllegalArgumentException() {
			//GIVEN
			when(userRepository.findById(anyInt())).thenThrow(new IllegalArgumentException());
			
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
				() -> userService.getUserById(1, request))
				.getMessage()).isEqualTo("Error while getting user profile");
		}

		@Test
		@Tag("UserServiceTest")
		@DisplayName("test getUserById should throw UnexpectedRollbackException on any RuntimeException")
		public void getUserByIdTestShouldThrowsUnexpectedRollbackExceptionOnAnyRuntimeException() {
			//GIVEN
			when(userRepository.findById(anyInt())).thenThrow(new RuntimeException());
			
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
				() -> userService.getUserById(1, request))
				.getMessage()).isEqualTo("Error while getting user profile");
		}
	}	
}	


