package com.poseidoninc.poseidon.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
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
import org.mockito.BDDMockito.Then;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.User;
import com.poseidoninc.poseidon.exception.ResourceConflictException;
import com.poseidoninc.poseidon.exception.ResourceNotFoundException;
import com.poseidoninc.poseidon.repositories.UserRepository;

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
		public void getUserByUserNameTestShouldRetrunExcpectedUser() {
			
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
		public void getUserByIdTestShouldRetrunExcpectedUser() {
			
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
		@DisplayName("test getUserById should throw IllegalArgumentException")
		public void getUserByIdTestShouldThrowsUnexpectedRollbackExceptionOnIllegalArgumentException() {
			//GIVEN
			when(userRepository.findById(anyInt())).thenThrow(new IllegalArgumentException());
			
			//WHEN
			//THEN
			assertThat(assertThrows(IllegalArgumentException.class,
				() -> userService.getUserById(1, request))
				.getMessage()).isEqualTo("Id must not be null");
		}

		@Test
		@Tag("UserServiceTest")
		@DisplayName("test getUserById should throw ResourceNotFoundException")
		public void getUserByIdTestShouldThrowsResourceNotFoundException() {
			//GIVEN
			when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(null));
			
			//WHEN
			//THEN
			assertThat(assertThrows(ResourceNotFoundException.class,
				() -> userService.getUserById(1, request))
				.getMessage()).isEqualTo("User not found");
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
		public void getUsersTesthouldRetrunExcpectedUsers() {
			
			//GIVEN
			List<User> expectedUsers = new ArrayList<>();
			user = new User();
			user.setId(1);
			user.setUsername("Aaa");
			user.setPassword("aaa1=Passwd");
			user.setFullname("AAA");
			user.setRole("USER");
			expectedUsers.add(user);
			user.setId(2);
			user.setUsername("Bbb");
			user.setPassword("bbb2=Passwd");
			user.setFullname("BBB");
			expectedUsers.add(user);
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
		@Tag("RegisteredServiceTest")
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
			user.setId(1);
			user.setUsername("Aaa");
			user.setPassword("aaa1=Passwd");
			user.setFullname("AAA");
			user.setRole("USER");
		}
		
		@AfterEach
		public void unSetForEachTests() {
			userService = null;
			user = null;
		}

		@ParameterizedTest(name = "id = {0}, userToSaveName = {1}, existsByUserName return {2}, saveUser should return user")
		@CsvSource(value = {"null, Aaa, false",
							"1, Aaa, true",
							"1, Bbb, false"}
							,nullValues = {"null"})
		@Tag("UserServiceTest")
		@DisplayName("test saveUser should return user")
		public void saveUserTestShouldReturnUser(Integer id, String userToSaveName, boolean existsByUserName) {
			
			//GIVEN
			User userToSave = new User();
			userToSave.setId(id);
			userToSave.setUsername(userToSaveName);
			userToSave.setPassword("aaa1=Passwd");
			userToSave.setFullname("AAA");
			userToSave.setRole("USER");
			
			when(userRepository.findById(nullable(Integer.class))).then(invocation -> {
				Integer index = invocation.getArgument(0);
				if (index == null) {
					throw new IllegalArgumentException ("Id must not be null");
				} else {
					return Optional.of(user); 
				}
			});
			lenient().when(userRepository.existsByUsername(anyString())).thenReturn(existsByUserName);
			ArgumentCaptor<User> userBeingSaved = ArgumentCaptor.forClass(User.class);
			when(userRepository.save(any(User.class))).thenReturn(userToSave);
			
			//WHEN
			User resultedUser = userService.saveUser(userToSave, request);
			
			//THEN
			verify(userRepository, times(1)).save(userBeingSaved.capture());
			passwordEncoder.matches("aaa1=Passwd", userBeingSaved.getValue().getPassword());
			assertThat(resultedUser).extracting(
					User::getUsername,
					User::getFullname,
					User::getRole)
				.containsExactly(
					userToSaveName,
					"AAA",
					"USER");	
		}
		
		@ParameterizedTest(name = "id = {0}, userToSaveName = {1}, existsByUserName return {2}, saveUser should throw ResourceConflictException")
		@CsvSource(value = {"null, Aaa, true",
							"1, Bbb, true"}
							,nullValues = {"null"})
		@Tag("UserServiceTest")
		@DisplayName("test saveUser should throw ResourceConflictException")
		public void saveUserTestShouldThrowsResourceConflictException(Integer id, String userToSaveName, boolean existsByUserName) {

			//GIVEN
			User userToSave = new User();
			userToSave.setId(id);
			userToSave.setUsername(userToSaveName);
			userToSave.setPassword("aaa1=Passwd");
			userToSave.setFullname("AAA");
			userToSave.setRole("USER");

			when(userRepository.findById(nullable(Integer.class))).thenReturn(Optional.of(user));
			lenient().when(userRepository.existsByUsername(anyString())).thenReturn(existsByUserName);

			//WHEN
			//THEN
			assertThat(assertThrows(ResourceConflictException.class,
					() -> userService.saveUser(userToSave, request))
					.getMessage()).isEqualTo("UserName already exists");
		}
		
		@Test
		@Tag("UserServiceTest")
		@DisplayName("test saveUser with unknow id should throw a ResourceNotFoundException")
		public void saveUserTestWithUnknownIdShouldThrowAResourceNotFoundException() {
			
			//GIVEN
			User userToSave = new User();
			userToSave.setId(1);
			userToSave.setUsername("Aaa");
			userToSave.setPassword("aaa1=Passwd");
			userToSave.setFullname("AAA");
			userToSave.setRole("USER");

			when(userRepository.findById(nullable(Integer.class))).thenThrow(new ResourceNotFoundException("User not found"));

			//WHEN
			//THEN
			assertThat(assertThrows(ResourceNotFoundException.class,
					() -> userService.saveUser(userToSave, request))
					.getMessage()).isEqualTo("User not found");
		}	
		
		@Test
		@Tag("UserServiceTest")
		@DisplayName("test saveUser should throw UnexpectedRollbackException on any RuntimeException")
		public void saveUserTestShouldThrowUnexpectedRollbackExceptionOnAnyRuntimeException() {
			
			//GIVEN
			User userToSave = new User();
			userToSave.setId(1);
			userToSave.setUsername("Aaa");
			userToSave.setPassword("aaa1=Passwd");
			userToSave.setFullname("AAA");
			userToSave.setRole("USER");

			when(userRepository.findById(nullable(Integer.class))).thenReturn(Optional.of(user));
			lenient().when(userRepository.existsByUsername(anyString())).thenReturn(true);
			when(userRepository.save(any(User.class))).thenThrow(new RuntimeException());

			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> userService.saveUser(userToSave, request))
					.getMessage()).isEqualTo("Error while saving user");
		}	
	}
	
	@Nested
	@Tag("deleteUserTests")
	@DisplayName("Tests for deleting users")
	@TestInstance(Lifecycle.PER_CLASS)
	class DeleteUserTests {
		
		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/user/delete/1");
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
			user.setId(1);
			user.setUsername("Aaa");
			user.setPassword("aaa1=Passwd");
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
		@DisplayName("test deleteUser should delete it")
		public void deleteUserTestShouldDeleteIt() {
			
			//GIVEN
			when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
			ArgumentCaptor<User> userBeingDeleted = ArgumentCaptor.forClass(User.class);
			doNothing().when(userRepository).delete(any(User.class));// Needed to Capture user
			
			//WHEN
			userService.deleteUser(1, request);
			
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
					"aaa1=Passwd",
					"AAA",
					"USER");	
		}
		
		@Test
		@Tag("UserServiceTest")
		@DisplayName("test deleteUser should throw ResourceNotFoundException")
		public void saveUserTestShouldThrowUnexpectedRollbackExceptionOnResourceNotFoundException() {

			//GIVEN
			when(userRepository.findById(anyInt())).thenThrow(new ResourceNotFoundException("User not found"));

			//WHEN
			//THEN
			assertThat(assertThrows(ResourceNotFoundException.class,
					() -> userService.deleteUser(2, request))
					.getMessage()).isEqualTo("User not found");
		}	

		@Test
		@Tag("UserServiceTest")
		@DisplayName("test deleteUser should throw UnexpectedRollbackException On Any RuntimeExpceptioin")
		public void deleteUserTestShouldThrowsUnexpectedRollbackExceptionOnAnyRuntimeException() {

			//GIVEN
			when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
			doThrow(new RuntimeException()).when(userRepository).delete(any(User.class));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> userService.deleteUser(1, request))
					.getMessage()).isEqualTo("Error while deleting user");
		}	
	}
	
}	


