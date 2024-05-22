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
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.UnexpectedRollbackException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * unit test class for the UserService.
 * @author olivier morel
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@InjectMocks
	private UserServiceImpl userService;
	
	@Mock
	private UserRepository userRepository;

	@Spy
	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	private User user;
	
	@Nested
	@Tag("getUserByUserNameTests")
	@DisplayName("Tests for getUserByUserName")
	class GetUserByUserNameTests {

		@AfterEach
		public void unSetForEachTests() {
			userService = null;
			user = null;
		}

		@Test
		@Tag("UserServiceTest")
		@DisplayName("test getUserByUsername should return expected user")
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
			User userResult = userService.getUserByUserName("Aaa");
			
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
			assertThat(assertThrows(ResourceNotFoundException.class,
				() -> userService.getUserByUserName("Aaa"))
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
				() -> userService.getUserByUserName("Aaa"))
				.getMessage()).isEqualTo("Error while getting user");
		}
	}

	@Nested
	@Tag("getUserByIdTests")
	@DisplayName("Tests for getting user by Id")
	class GetUserByIdTests {
		
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
			User userResult = userService.getUserById(1);
			
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
				() -> userService.getUserById(null))
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
				() -> userService.getUserById(1))
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
				() -> userService.getUserById(1))
				.getMessage()).isEqualTo("Error while getting user");
		}
	}
	
	@Nested
	@Tag("getUserByIdWithBlankPasswdTests")
	@DisplayName("Tests for getting user by Id with blank passwd")
	class GetUserByIdWithBlankPasswdTests {

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
			User userResult = userService.getUserByIdWithBlankPasswd(1);
			
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
				() -> userService.getUserByIdWithBlankPasswd(null))
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
				() -> userService.getUserByIdWithBlankPasswd(1))
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
				() -> userService.getUserByIdWithBlankPasswd(1))
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
		}

		@AfterAll
		public void unSetForAllTests() {
			pageRequest = null;
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
			List<User> givenUsers = new ArrayList<>();
			user = new User();
			user.setId(1);
			user.setUsername("Aaa");
			user.setPassword("apw1=Passwd");
			user.setFullname("AAA");
			user.setRole("USER");
			givenUsers.add(user);

			User user2 = new User();			
			user2.setId(2);
			user2.setUsername("Bbb");
			user2.setPassword("bpw2=Passwd");
			user2.setFullname("BBB");
			user2.setRole("USER");
			givenUsers.add(user2);
			when(userRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<User>(givenUsers, pageRequest, 2));
			
			//WHEN
			Page<User> resultedUsers = userService.getUsers(pageRequest);
			
			//THEN
			assertThat(resultedUsers)
					.extracting(
							User::getId,
							User::getUsername,
							User::getPassword,
							User::getFullname,
							User::getRole)
					.containsExactly(
							tuple(1, "Aaa", "", "AAA", "USER"),
							tuple(2, "Bbb", "", "BBB", "USER"));
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
					() -> userService.getUsers(pageRequest))
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
					() -> userService.getUsers(pageRequest))
					.getMessage()).isEqualTo("Error while getting Users");
		}
	}
	
	@Nested
	@Tag("saveUserTests")
	@DisplayName("Tests for saving users")
	class SaveUserTests {
		
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
			User userExpected = new User();
			userExpected.setId(1);
			userExpected.setUsername("Aaa");
			userExpected.setPassword("");
			userExpected.setFullname("AAA");
			userExpected.setRole("USER");

			ArgumentCaptor<User> userBeingSaved = ArgumentCaptor.forClass(User.class);
			when(userRepository.save(any(User.class))).thenReturn(userExpected);
			
			//WHEN
			User resultedUser = userService.saveUser(user);
			
			//THEN
			verify(userRepository, times(1)).save(userBeingSaved.capture());
			assertThat(passwordEncoder.matches("apw1=Passwd", userBeingSaved.getValue().getPassword())).isTrue();
			assertThat(resultedUser).extracting(
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
		@DisplayName("test saveUser should throw DataIntegrityViolationException")
		public void saveUserTestShouldThrowsDataIntegrityViolationException() {

			//GIVEN
			when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("Unique index or primary key violation"));

			//WHEN
			//THEN
			assertThat(assertThrows(DataIntegrityViolationException.class,
					() -> userService.saveUser(user))
					.getMessage()).isEqualTo("Username already exists");
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
					() -> userService.saveUser(user))
					.getMessage()).isEqualTo("Error while saving user");
		}	
	}
	
	@Nested
	@Tag("deleteUserTests")
	@DisplayName("Tests for deleting users")
	class DeleteUserTests {

		@BeforeEach
		public void setUpForEachTest() {
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
		}

		@Test
		@Tag("UserServiceTest")
		@DisplayName("test deleteUser by Id should delete it")
		public void deleteUserByIdTestShouldDeleteIt() {
			
			//GIVEN
			when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
			ArgumentCaptor<User> userBeingDeleted = ArgumentCaptor.forClass(User.class);
			doNothing().when(userRepository).delete(any(User.class));// Needed to Capture user
			
			//WHEN
			userService.deleteUserById(1);
			
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
			when(userRepository.findById(anyInt())).thenThrow(new UnexpectedRollbackException("Error while getting user"));

			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> userService.deleteUserById(2))
					.getMessage()).isEqualTo("Error while deleting user");
		}	

		@Test
		@Tag("UserServiceTest")
		@DisplayName("test deleteUser by Id should throw UnexpectedRollbackException On InvalidDataAccessApiUsageException")
		public void deleteUserByIdTestShouldThrowsUnexpectedRollbackExceptionOnInvalidDataAccessApiUsageException() {

			//GIVEN
			when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
			doThrow(new InvalidDataAccessApiUsageException("Entity must not be null")).when(userRepository).delete(any(User.class));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> userService.deleteUserById(1))
					.getMessage()).isEqualTo("Error while deleting user");
		}	
		
		
		@Test
		@Tag("UserServiceTest")
		@DisplayName("test deleteUser by Id should throw UnexpectedRollbackException On Any RuntimeException")
		public void deleteUserByIdTestShouldThrowsUnexpectedRollbackExceptionOnAnyRuntimeException() {

			//GIVEN
			when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
			doThrow(new RuntimeException()).when(userRepository).delete(any(User.class));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> userService.deleteUserById(1))
					.getMessage()).isEqualTo("Error while deleting user");
		}	
	}
}	


