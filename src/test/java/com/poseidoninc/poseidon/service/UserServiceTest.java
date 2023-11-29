package com.poseidoninc.poseidon.service;

import com.poseidoninc.poseidon.domain.User;
import com.poseidoninc.poseidon.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@InjectMocks
	private UserServiceImpl userService;
	
	@Mock
	private UserRepository userRepository;

	@Spy
	private final RequestService requestService = new RequestServiceImpl();

	@Spy
	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	private MockHttpServletRequest requestMock;
	private WebRequest request;
	private User user;

	
	@Nested
	@Tag("getUserByUserNameTests")
	@DisplayName("Tests for getUserByUserName")
	@TestInstance(Lifecycle.PER_CLASS)
	class GetUserByUserNameTests {
		
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
		public void getUserByUserNameTestShouldReturnExpectedUser() {
			
			//GIVEN
			user = new User();
			user.setId(1);
			user.setUsername("Aaa");
			user.setPassword("apw1=Passwd");
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
					"apw1=Passwd",
					"AAA",
					"USER");	
		}
			
		@Test
		@Tag("UserServiceTest")
		@DisplayName("test getUserByUserName should throw UnexpectedRollbackException on ResourceNotFoundException")
		public void getUserByUserNameTestShouldThrowsUnexpectedRollbackExceptionOnResourceNotFoundException() {
			//GIVEN
			when(userRepository.findByUsername(anyString())).thenReturn(null);
			
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
				() -> userService.getUserByUserName("Aaa", request))
				.getMessage()).isEqualTo("Error while getting user");
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
				() -> userService.getUserByUserName("Aaa", request))
				.getMessage()).isEqualTo("Error while getting user");
		}
	}

	@Nested
	@Tag("getUserByIdTests")
	@DisplayName("Tests for getting user by Id")
	@TestInstance(Lifecycle.PER_CLASS)
	class GetUserByIdTests {
		
		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/user/getById/1");
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
		public void getUserByIdTestShouldReturnExpectedUser() {
			
			//GIVEN
			user = new User();
			user.setId(1);
			user.setUsername("Aaa");
			user.setPassword("apw1=Passwd");
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
					"apw1=Passwd",
					"AAA",
					"USER");	
		}
		
		@Test
		@Tag("UserServiceTest")
		@DisplayName("test getUserById should throw UnexpectedRollbackException on InvalidDataAccessApiUsageException")
		public void getUserByIdTestShouldThrowsUnexpectedRollbackExceptionOnInvalidDataAccessApiUsageException() {
			//GIVEN
			when(userRepository.findById(nullable(Integer.class))).thenThrow(new InvalidDataAccessApiUsageException("The given id must not be null"));
			
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
				() -> userService.getUserById(null, request))
				.getMessage()).isEqualTo("Error while getting user");
		}

		@Test
		@Tag("UserServiceTest")
		@DisplayName("test getUserById should throw UnexpectedRollbackException on ResourceNotFoundException")
		public void getUserByIdTestShouldThrowsUnexpectedRollbackExceptionOnResourceNotFoundException() {
			//GIVEN
			when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
			
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
				() -> userService.getUserById(1, request))
				.getMessage()).isEqualTo("Error while getting user");
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
				.getMessage()).isEqualTo("Error while getting user");
		}
	}
	
	@Nested
	@Tag("getUserByIdWithBlankPasswdTests")
	@DisplayName("Tests for getting user by Id with blank passwd")
	@TestInstance(Lifecycle.PER_CLASS)
	class GetUserByIdWithBlankPasswdTests {
		
		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/user/getById/1");
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
		@DisplayName("test getUserByIdWithBlankPasswd should return expected user")
		public void getUserByIdWithBlankPasswdTestShouldReturnExpectedUser() {
			
			//GIVEN
			user = new User();
			user.setId(1);
			user.setUsername("Aaa");
			user.setPassword("apw1=Passwd");
			user.setFullname("AAA");
			user.setRole("USER");
			when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
			
			//WHEN
			User userResult = userService.getUserByIdWithBlankPasswd(1, request);
			
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
					"",
					"AAA",
					"USER");	
		}
		
		@Test
		@Tag("UserServiceTest")
		@DisplayName("test getUserByIdWithBlankPasswd should throw UnexpectedRollbackException on InvalidDataAccessApiUsageException")
		public void getUserByIdWithBlankPasswdTestShouldThrowsUnexpectedRollbackExceptionOnInvalidDataAccessApiUsageException() {
			//GIVEN
			when(userRepository.findById(isNull())).thenThrow(new InvalidDataAccessApiUsageException("The given id must not be null"));
			
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
				() -> userService.getUserByIdWithBlankPasswd(null, request))
				.getMessage()).isEqualTo("Error while getting user");
		}

		@Test
		@Tag("UserServiceTest")
		@DisplayName("test getUserByIdWithBlankPasswd should throw UnexpectedRollbackException on ResourceNotFoundException")
		public void getUserByIdWithBlankPasswdTestShouldThrowUnexpectedRollbackExceptionOnResourceNotFoundException() {
			//GIVEN
			when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
			
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
				() -> userService.getUserByIdWithBlankPasswd(1, request))
				.getMessage()).isEqualTo("Error while getting user");
		}
		
		@Test
		@Tag("UserServiceTest")
		@DisplayName("test getUserByIdWithBlankPasswd should throw UnexpectedRollbackException on any RuntimeException")
		public void getUserByIdWithBlankPasswdTestShouldThrowsUnexpectedRollbackExceptionOnAnyRuntimeException() {
			//GIVEN
			when(userRepository.findById(anyInt())).thenThrow(new RuntimeException());
			
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
				() -> userService.getUserByIdWithBlankPasswd(1, request))
				.getMessage()).isEqualTo("Error while getting user");
		}
	}
	
	@Nested
	@Tag("getUsersTests")
	@DisplayName("Tests for getting users")
	@TestInstance(Lifecycle.PER_CLASS)
	class GetUsersTests {
		
		private Pageable pageRequest;
		
		@BeforeAll
		public void setUpForAllTests() {
			pageRequest = Pageable.unpaged();
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/user/getUsers");
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
			userService = null;
			user = null;
		}

		@Test
		@Tag("UserServiceTest")
		@DisplayName("test getUsers should return users")
		public void getUsersTestShouldReturnExpectedUsers() {
			
			//GIVEN
			List<User> expectedUsers = new ArrayList<>();
			user = new User();
			user.setId(1);
			user.setUsername("Aaa");
			user.setPassword("apw1=Passwd");
			user.setFullname("AAA");
			user.setRole("USER");
			expectedUsers.add(user);
			User user2 = new User();			
			user2.setId(2);
			user2.setUsername("Bbb");
			user2.setPassword("bpw2=Passwd");
			user2.setFullname("BBB");
			expectedUsers.add(user2);
			when(userRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<User>(expectedUsers, pageRequest, 2));
			
			//WHEN
			Page<User> resultedUsers = userService.getUsers(pageRequest, request);
			
			//THEN
			assertThat(resultedUsers).containsExactlyElementsOf(expectedUsers);
		}
		
		@Test
		@Tag("UserServiceTest")
		@DisplayName("test getUsers should throw UnexpectedRollbackException on NullPointerException")
		public void getUsersTestShouldThrowsUnexpectedRollbackExceptionOnNullPointerException() {
			//GIVEN
			when(userRepository.findAll(any(Pageable.class))).thenThrow(new NullPointerException());
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> userService.getUsers(pageRequest, request))
					.getMessage()).isEqualTo("Error while getting Users");
		}
		
		@Test
		@Tag("UserServiceTest")
		@DisplayName("test getUsers should throw UnexpectedRollbackException on any RuntimeException")
		public void getUsersTestShouldThrowsUnexpectedRollbackExceptionOnAnyRuntimeException() {
			//GIVEN
			when(userRepository.findAll(any(Pageable.class))).thenThrow(new RuntimeException());
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> userService.getUsers(pageRequest, request))
					.getMessage()).isEqualTo("Error while getting Users");
		}
	}
	
	@Nested
	@Tag("saveUserTests")
	@DisplayName("Tests for saving users")
	@TestInstance(Lifecycle.PER_CLASS)
	class SaveUserTests {
		
		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/user/saveUser/");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}
		
		@BeforeEach
		public void setUpForEachTest() {
			user = new User();
			user.setUsername("Aaa");
			user.setPassword("apw1=Passwd");
			user.setFullname("AAA");
			user.setRole("USER");
		}
		
		@AfterEach
		public void unSetForEachTests() {
			userService = null;
			user = null;
		}

		@Test
		@Tag("UserServiceTest")
		@DisplayName("test saveUser should persist and return user")
		public void saveUserTestShouldPersistAndReturnUser() {
			
			//GIVEN
			ArgumentCaptor<User> userBeingSaved = ArgumentCaptor.forClass(User.class);
			when(userRepository.save(any(User.class))).then(invocation -> {
				User userSaved = invocation.getArgument(0);
				userSaved.setId(1);
				return userSaved;
				});
			
			//WHEN
			User resultedUser = userService.saveUser(user, request);
			
			//THEN
			verify(userRepository, times(1)).save(userBeingSaved.capture());
			assertThat(passwordEncoder.matches("apw1=Passwd", userBeingSaved.getValue().getPassword())).isTrue();
			assertThat(resultedUser).extracting(
					User::getId,
					User::getUsername,
					User::getFullname,
					User::getRole)
				.containsExactly(
					1,	
					"Aaa",
					"AAA",
					"USER");	
		}
		
		@Test
		@Tag("UserServiceTest")
		@DisplayName("test saveUser should throw DataIntegrityViolationException")
		public void saveUserTestShouldThrowsDataIntegrityViolationException() {

			//GIVEN
			when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("Unique index or primary key violation"));

			//WHEN
			//THEN
			assertThat(assertThrows(DataIntegrityViolationException.class,
					() -> userService.saveUser(user, request))
					.getMessage()).isEqualTo("Username already exist, try another one");
		}
		
		@Test
		@Tag("UserServiceTest")
		@DisplayName("test saveUser should throw UnexpectedRollbackException on any RuntimeException")
		public void saveUserTestShouldThrowUnexpectedRollbackExceptionOnAnyRuntimeException() {
			
			//GIVEN
			when(userRepository.save(any(User.class))).thenThrow(new RuntimeException());

			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> userService.saveUser(user, request))
					.getMessage()).isEqualTo("Error while saving user");
		}	
	}
	
	@Nested
	@Tag("deleteUserTests")
	@DisplayName("Tests for deleting users")
	class DeleteUserTests {

		@BeforeEach
		public void setUpForEachTest() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			user = new User();
			user.setId(1);
			user.setUsername("Aaa");
			user.setPassword("apw1=Passwd");
			user.setFullname("AAA");
			user.setRole("USER");
		}
		
		@AfterEach
		public void unSetForEachTests() {
			userService = null;
			user = null;
			requestMock = null;
			request = null;
		}

		@Test
		@Tag("UserServiceTest")
		@DisplayName("test deleteUser by Id should delete it")
		public void deleteUserByIdTestShouldDeleteIt() {
			
			//GIVEN
			requestMock.setRequestURI("/user/delete/1");
			request = new ServletWebRequest(requestMock);
			when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
			ArgumentCaptor<User> userBeingDeleted = ArgumentCaptor.forClass(User.class);
			doNothing().when(userRepository).delete(any(User.class));// Needed to Capture user
			
			//WHEN
			userService.deleteUserById(1, request);
			
			//THEN
			verify(userRepository, times(1)).delete(userBeingDeleted.capture());
			assertThat(userBeingDeleted.getValue()).extracting(
					User::getId,
					User::getUsername,
					User::getPassword,
					User::getFullname,
					User::getRole)
				.containsExactly(
					1,
					"Aaa",
					"apw1=Passwd",
					"AAA",
					"USER");	
		}
		
		@Test
		@Tag("UserServiceTest")
		@DisplayName("test deleteUser by Id should throw UnexpectedRollbackException on UnexpectedRollbackException")
		public void deleteUserByIdTestShouldThrowUnexpectedRollbackExceptionOnUnexpectedRollbackException() {

			//GIVEN
			requestMock.setRequestURI("/user/delete/2");
			request = new ServletWebRequest(requestMock);
			when(userRepository.findById(anyInt())).thenThrow(new UnexpectedRollbackException("Error while getting user"));

			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> userService.deleteUserById(2, request))
					.getMessage()).isEqualTo("Error while deleting user");
		}	

		@Test
		@Tag("UserServiceTest")
		@DisplayName("test deleteUser by Id should throw UnexpectedRollbackException On InvalidDataAccessApiUsageException")
		public void deleteUserByIdTestShouldThrowsUnexpectedRollbackExceptionOnInvalidDataAccessApiUsageException() {

			//GIVEN
			requestMock.setRequestURI("/user/delete/1");
			request = new ServletWebRequest(requestMock);
			when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
			doThrow(new InvalidDataAccessApiUsageException("Entity must not be null")).when(userRepository).delete(any(User.class));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> userService.deleteUserById(1, request))
					.getMessage()).isEqualTo("Error while deleting user");
		}	
		
		
		@Test
		@Tag("UserServiceTest")
		@DisplayName("test deleteUser by Id should throw UnexpectedRollbackException On Any RuntimeException")
		public void deleteUserByIdTestShouldThrowsUnexpectedRollbackExceptionOnAnyRuntimeException() {

			//GIVEN
			requestMock.setRequestURI("/user/delete/1");
			request = new ServletWebRequest(requestMock);
			when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
			doThrow(new RuntimeException()).when(userRepository).delete(any(User.class));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> userService.deleteUserById(1, request))
					.getMessage()).isEqualTo("Error while deleting user");
		}	
	}
}	


