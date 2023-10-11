package com.poseidoninc.poseidon.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import com.poseidoninc.poseidon.domain.User;
import com.poseidoninc.poseidon.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindingResult;
import org.springframework.ui.Model;
import org.springframework.web.context.request.WebRequest;

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

	@Mock
	private WebRequest request;

	@AfterEach
	public void unsetForEachTest() {
		userController = null;
	}

	@Test
	@Tag("UserControllerTest")
	@DisplayName("test home should return \"user list\" ")
	public void homeTest() {
		
		//GIVEN
		//WHEN
		String html = userController.home(model, request);
		
		//THEN
		assertThat(html).isEqualTo("user/list");
	}

	@Test
	@Tag("UserControllerTest")
	@DisplayName("test  addUser should return \"user/add\"")
	public void userAddTestShouldReturnStringUserAdd() {

		//GIVEN
		User user = new User();

		//WHEN
		String html = userController.addUser(user);

		//THEN
		assertThat(html).isEqualTo("user/add");
	}
	@Test
	@Tag("UserControllerTest")
	@DisplayName("test validate should return \"redirect:/user/list\"")
	public void valisateTestShouldReturnStringRedirectUserList() {

		//GIVEN
		User user = new User();
		when(bindingResult.hasErrors()).thenReturn(false);

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
		when(userService.saveUser(any(User.class), any(WebRequest.class))).thenThrow(new DataIntegrityViolationException(""));

		//WHEN
		String html = userController.validate(user, bindingResult, request);

		//THEN
		assertThat(html).isEqualTo("user/add");
	}

	@Test
	@Tag("UserControllerTest")
	@DisplayName("test showUpdateForm should return \"user/update\"")
	public void showUpdateFormTestShouldReturnStringUserUpdate() {

		//GIVEN
		User user = new User();
		when(userService.getUserByIdWithBlankPasswd(anyInt(), any(WebRequest.class))).thenReturn(user);

		//WHEN
		String html = userController.showUpdateForm(1, model, request);

		//THEN
		assertThat(html).isEqualTo("user/update");
	}

	@Test
	@Tag("UserControllerTest")
	@DisplayName("test updateUser should return \"redirect:/user/list\"")
	public void updateUserTestShouldReturnStringRedirectUserList() {

		//GIVEN
		User user = new User();
		when(bindingResult.hasErrors()).thenReturn(false);

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
	public void updateUserTestShouldReturnStringUserAddDataIntegrityViolationException() {

		//GIVEN
		User user = new User();
		when(bindingResult.hasErrors()).thenReturn(false);
		when(userService.saveUser(any(User.class), any(WebRequest.class))).thenThrow(new DataIntegrityViolationException(""));

		//WHEN
		String html = userController.updateUser(user, bindingResult, request);

		//THEN
		assertThat(html).isEqualTo("user/update");
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

}
