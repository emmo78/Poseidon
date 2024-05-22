package com.poseidoninc.poseidon.controller;

import com.poseidoninc.poseidon.domain.User;
import com.poseidoninc.poseidon.service.RequestService;
import com.poseidoninc.poseidon.service.RequestServiceImpl;
import com.poseidoninc.poseidon.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * unit test class for the UserController.
 * @author olivier morel
 */
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

	@InjectMocks
	private UserController userController;

	@Mock
	private UserService userService;
	
	@Mock
	private BindingResult bindingResult;
	
	@Mock
	private Model model;

	@Spy
	private final RequestService requestService = new RequestServiceImpl();

	private MockHttpServletRequest requestMock;
	private WebRequest request;

	@AfterEach
	public void unsetForEachTest() {
		userService = null;
		userController = null;
	}

	@Nested
	@Tag("homeUserControllerTests")
	@DisplayName("Tests for /user/list")
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class HomeUserControllerTests {

		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/user/list");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}

		@Test
		@Tag("UserControllerTest")
		@DisplayName("test home should return \"user/list\" ")
		public void homeTestTestShouldReturnStringUserList() {

			//GIVEN
			List<User> givenUsers = new ArrayList<>();
			User user = new User();
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

			ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
			ArgumentCaptor<Iterable<User>> iterableArgumentCaptor = ArgumentCaptor.forClass(Iterable.class);
			when(userService.getUsers(any(Pageable.class))).thenReturn(new PageImpl<User>(givenUsers).map(u -> {u.setPassword(""); return u;}));

			//WHEN
			String html = userController.home(model, request);

			//THEN
			verify(model).addAttribute(stringArgumentCaptor.capture(), iterableArgumentCaptor.capture()); //times(1) is used by default
			assertThat(stringArgumentCaptor.getValue()).isEqualTo("users");
			assertThat(iterableArgumentCaptor.getValue())
					.extracting(
							User::getId,
							User::getUsername,
							User::getPassword,
							User::getFullname,
							User::getRole)
					.containsExactly(
							tuple(1, "Aaa", "", "AAA", "USER"),
							tuple(2, "Bbb", "", "BBB", "USER"));
			assertThat(html).isEqualTo("user/list");
		}

		@Test
		@Tag("UserControllerTest")
		@DisplayName("test home should throw UnexpectedRollbackException")
		public void homeTestShouldThrowUnexpectedRollbackException() {

			//GIVEN
			when(userService.getUsers(any(Pageable.class))).thenThrow(new UnexpectedRollbackException("Error while getting users"));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> userController.home(model, request))
					.getMessage()).isEqualTo("Error while getting users");
		}
	}

	@Test
	@Tag("UserControllerTest")
	@DisplayName("test addUser should return \"user/add\"")
	public void userAddTestShouldReturnStringUserAdd() {

		//GIVEN
		User user = new User();

		//WHEN
		String html = userController.addUser(user);

		//THEN
		assertThat(html).isEqualTo("user/add");
	}

	@Nested
	@Tag("validateUserControllerTests")
	@DisplayName("Tests for /user/validate")
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class ValidateUserControllerTests {

		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/user/validate/");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}

		@Test
		@Tag("UserControllerTest")
		@DisplayName("test validate should return \"redirect:/user/list\"")
		public void validateTestShouldReturnStringRedirectUserList() {

			//GIVEN
			User user = new User();
			when(bindingResult.hasErrors()).thenReturn(false);
			when(userService.saveUser(any(User.class))).thenReturn(user);

			//WHEN
			String html = userController.validate(user, bindingResult, request);

			//THEN
			assertThat(html).isEqualTo("redirect:/user/list");
		}

		@Test
		@Tag("UserControllerTest")
		@DisplayName("test validate should return \"user/add\" on BindingResultError")
		public void validateTestShouldReturnStringUserAddOnBindingResulError() {

			//GIVEN
			User user = new User();
			when(bindingResult.hasErrors()).thenReturn(true);

			//WHEN
			String html = userController.validate(user, bindingResult, request);

			//THEN
			assertThat(html).isEqualTo("user/add");
		}

		@Test
		@Tag("UserControllerTest")
		@DisplayName("test validate should return \"user/add\" on DataIntegrityViolationException")
		public void validateTestShouldReturnStringUserAddDataIntegrityViolationException() {

			//GIVEN
			User user = new User();
			when(bindingResult.hasErrors()).thenReturn(false);
			when(userService.saveUser(any(User.class))).thenThrow(new DataIntegrityViolationException(""));

			//WHEN
			String html = userController.validate(user, bindingResult, request);

			//THEN
			assertThat(html).isEqualTo("user/add");
		}

		@Test
		@Tag("UserControllerTest")
		@DisplayName("test validate should throw UnexpectedRollbackException")
		public void validateTestShouldThrowUnexpectedRollbackException() {

			//GIVEN
			User user = new User();
			when(bindingResult.hasErrors()).thenReturn(false);
			when(userService.saveUser(any(User.class))).thenThrow(new UnexpectedRollbackException("Error while saving user"));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> userController.validate(user, bindingResult, request))
					.getMessage()).isEqualTo("Error while saving user");
		}
	}

	@Nested
	@Tag("showUpdateFormUserControllerTests")
	@DisplayName("Tests for /user/update/{id}")
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class ShowUpdateFormUserControllerTests {

		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/user/update/1");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}

		@Test
		@Tag("UserControllerTest")
		@DisplayName("test showUpdateForm should return \"user/update\"")
		public void showUpdateFormTestShouldReturnStringUserUpdate() {

			//GIVEN
			User user = new User();
			user.setId(1);
			user.setUsername("Aaa");
			user.setPassword("apw1=Passwd");
			user.setFullname("AAA");
			user.setRole("USER");
			ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
			ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
			when(userService.getUserByIdWithBlankPasswd(anyInt())).thenReturn(user.builder().id(1).username("Aaa").password("").fullname("AAA").role("USER").build());

			//WHEN
			String html = userController.showUpdateForm(1, model, request);

			//THEN
			verify(model).addAttribute(stringArgumentCaptor.capture(), userArgumentCaptor.capture()); //times(1) is used by default
			assertThat(stringArgumentCaptor.getValue()).isEqualTo("user");
			assertThat(userArgumentCaptor.getValue()).extracting(
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
							"USER");	;
			assertThat(html).isEqualTo("user/update");
		}

		@Test
		@Tag("UserControllerTest")
		@DisplayName("test showUpdateForm should throw UnexpectedRollbackException")
		public void showUpdateFormTestShouldThrowUnexpectedRollbackException() {

			//GIVEN
			when(userService.getUserByIdWithBlankPasswd(anyInt())).thenThrow(new UnexpectedRollbackException("Error while getting user"));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> userController.showUpdateForm(1, model, request))
					.getMessage()).isEqualTo("Error while getting user");
		}
	}

	@Nested
	@Tag("updateUserUserControllerTests")
	@DisplayName("Tests for /user/update")
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class updateUserUserControllerTests {

		@BeforeAll
		public void setUpForAllTests() {
			requestMock = new MockHttpServletRequest();
			requestMock.setServerName("http://localhost:8080");
			requestMock.setRequestURI("/user/update/");
			request = new ServletWebRequest(requestMock);
		}

		@AfterAll
		public void unSetForAllTests() {
			requestMock = null;
			request = null;
		}

		@Test
		@Tag("UserControllerTest")
		@DisplayName("test updateUser should return \"redirect:/user/list\"")
		public void updateUserTestShouldReturnStringRedirectUserList() {

			//GIVEN
			User user = new User();
			when(bindingResult.hasErrors()).thenReturn(false);
			when(userService.saveUser(any(User.class))).thenReturn(user);

			//WHEN
			String html = userController.updateUser(user, bindingResult, request);

			//THEN
			assertThat(html).isEqualTo("redirect:/user/list");
		}

		@Test
		@Tag("UserControllerTest")
		@DisplayName("test updateUser should return \"user/update\" on BindingResultError")
		public void updateUserTestShouldReturnStringUserUpdateOnBindingResulError() {

			//GIVEN
			User user = new User();
			when(bindingResult.hasErrors()).thenReturn(true);

			//WHEN
			String html = userController.updateUser(user, bindingResult, request);

			//THEN
			assertThat(html).isEqualTo("user/update");
		}

		@Test
		@Tag("UserControllerTest")
		@DisplayName("test updateUser should return \"user/update\" on DataIntegrityViolationException")
		public void updateUserTestShouldReturnStringUserUpdateDataIntegrityViolationException() {

			//GIVEN
			User user = new User();
			when(bindingResult.hasErrors()).thenReturn(false);
			when(userService.saveUser(any(User.class))).thenThrow(new DataIntegrityViolationException(""));

			//WHEN
			String html = userController.updateUser(user, bindingResult, request);

			//THEN
			assertThat(html).isEqualTo("user/update");
		}

		@Test
		@Tag("UserControllerTest")
		@DisplayName("test update User should throw UnexpectedRollbackException")
		public void updateUserTestShouldThrowUnexpectedRollbackException() {

			//GIVEN
			User user = new User();
			when(bindingResult.hasErrors()).thenReturn(false);
			when(userService.saveUser(any(User.class))).thenThrow(new UnexpectedRollbackException("Error while saving user"));
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> userController.updateUser(user, bindingResult, request))
					.getMessage()).isEqualTo("Error while saving user");
		}
	}

	@Nested
	@Tag("deleteUserUserControllerTests")
	@DisplayName("Tests for /user/delete/{id}")
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class deleteUserUserControllerTests {

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

		@Test
		@Tag("UserControllerTest")
		@DisplayName("test deleteUser should return \"redirect:/user/list\"")
		public void deleteUserTestShouldReturnStringRedirectUserList() {
	
			//GIVEN
			//WHEN
			String html = userController.deleteUser(1, request);
	
			//THEN
			assertThat(html).isEqualTo("redirect:/user/list");
		}

		@Test
		@Tag("UserControllerTest")
		@DisplayName("test delete User should throw UnexpectedRollbackException")
		public void deleteUserTestShouldThrowUnexpectedRollbackException() {

			//GIVEN
			doThrow(new UnexpectedRollbackException("Error while deleting user")).when(userService).deleteUserById(anyInt());
			//WHEN
			//THEN
			assertThat(assertThrows(UnexpectedRollbackException.class,
					() -> userController.deleteUser(1, request))
					.getMessage()).isEqualTo("Error while deleting user");
		}
	}
}
