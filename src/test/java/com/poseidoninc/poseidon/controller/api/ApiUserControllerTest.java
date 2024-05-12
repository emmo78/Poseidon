package com.poseidoninc.poseidon.controller.api;

import com.poseidoninc.poseidon.domain.User;
import com.poseidoninc.poseidon.exception.BadRequestException;
import com.poseidoninc.poseidon.service.UserService;
import com.poseidoninc.poseidon.service.RequestService;
import com.poseidoninc.poseidon.service.RequestServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApiUserControllerTest {

    @InjectMocks
    private ApiUserController apiUserController;

    @Mock
    private UserService userService;

    @Spy
    private final RequestService requestService = new RequestServiceImpl();

    private MockHttpServletRequest requestMock;
    private WebRequest request;
    private User user;

    @AfterEach
    public void unsetForEachTest() {
        userService = null;
        apiUserController = null;
        user = null;
    }

    @Nested
    @Tag("getUsers")
    @DisplayName("Tests for /api/user/list")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GetUsersTests {

        private Pageable pageRequest;

        @BeforeAll
        public void setUpForAllTests() {
            pageRequest = Pageable.unpaged();
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("GET");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/user/list");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            pageRequest = null;
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("ApiUserControllerTest")
        @DisplayName("test getUsers should return a Success ResponseEntity With Iterable Of User")
        public void getUsersTestShouldReturnSuccessResponseEntityWithIterableOfUser() {

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

            when(userService.getUsers(any(Pageable.class))).thenReturn(new PageImpl<User>(givenUsers, pageRequest, 2).map(user -> {user.setPassword(""); return user;}));

            //WHEN
            ResponseEntity<Iterable<User>> responseEntity = apiUserController.getUsers(request);

            //THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            Iterable<User> resultUsers = responseEntity.getBody();
            assertThat(resultUsers).isNotNull();
            assertThat(resultUsers)
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
        @Tag("ApiUserControllerTest")
        @DisplayName("test getUsers should throw UnexpectedRollbackException")
        public void getUsersTestShouldThrowUnexpectedRollbackException() {

            //GIVEN
            when(userService.getUsers(any(Pageable.class))).thenThrow(new UnexpectedRollbackException("Error while getting users"));

            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiUserController.getUsers(request))
                    .getMessage()).isEqualTo("Error while getting users");
        }
    }

    @Nested
    @Tag("createUser")
    @DisplayName("Tests for /api/user/create")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class CreateUserTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("POST");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/user/create");
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
            user.setPassword("apw1=Passwd");
            user.setFullname("AAA");
            user.setRole("USER");
        }

        @Test
        @Tag("ApiUserControllerTest")
        @DisplayName("test createUser should return a Success ResponseEntity With Saved User")
        public void createUserTestShouldReturnASuccessResponseEntityWithSavedUser() {

            //GIVEN
            Optional<User> optionalUser = Optional.of(user);
            User userExpected = new User();
            userExpected.setId(1);
            userExpected.setUsername("Aaa");
            userExpected.setPassword("");
            userExpected.setFullname("AAA");
            userExpected.setRole("USER");
            when(userService.saveUser(any(User.class))).thenReturn(userExpected);

            //WHEN
            ResponseEntity<User> responseEntity = null;
            try {
                responseEntity = apiUserController.createUser(optionalUser, request);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            User resultUser = responseEntity.getBody();
            assertThat(resultUser).isNotNull();
            assertThat(resultUser)
                    .extracting(
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
                            "USER"
                    );
        }

        @Test
        @Tag("ApiUserControllerTest")
        @DisplayName("test createUser should throw BadRequestException")
        public void createUserTestShouldThrowBadRequestException() {

            //GIVEN
            Optional<User> optionalUser = Optional.empty();

            //WHEN
            //THEN
            assertThat(assertThrows(BadRequestException.class,
                    () -> apiUserController.createUser(optionalUser, request))
                    .getMessage()).isEqualTo("Correct request should be a json User body");
        }

        @Test
        @Tag("ApiUserControllerTest")
        @DisplayName("test createUser should throw UnexpectedRollbackException")
        public void createUserTestShouldThrowMUnexpectedRollbackException() {

            //GIVEN
            Optional<User> optionalUser = Optional.of(user);
            when(userService.saveUser(any(User.class))).thenThrow(new UnexpectedRollbackException("Error while saving user"));

            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiUserController.createUser(optionalUser, request))
                    .getMessage()).isEqualTo("Error while saving user");
        }
    }

    @Nested
    @Tag("getUserById")
    @DisplayName("Tests for /api/user/update/{id}")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class getUserByIdTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("GET");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/user/update/1");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("ApiUserControllerTest")
        @DisplayName("test getUserById should return a Success ResponseEntity With User")
        public void getUserByIdTestShouldReturnASuccessResponseEntityWithUser() {

            //GIVEN
            user = new User();
            user.setId(1);
            user.setUsername("Aaa");
            user.setPassword("");
            user.setFullname("AAA");
            user.setRole("USER");

            when(userService.getUserByIdWithBlankPasswd(anyInt())).thenReturn(user);

            //WHEN
            ResponseEntity<User> responseEntity = apiUserController.getUserById(1, request);

            //THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            User resultUser = responseEntity.getBody();
            assertThat(resultUser).isNotNull();
            assertThat(resultUser)
                    .extracting(
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
                            "USER"
                    );
        }

        @Test
        @Tag("ApiUserControllerTest")
        @DisplayName("test getUserById should throw UnexpectedRollbackException")
        public void getUserByIdTestShouldThrowUnexpectedRollbackException() {

            //GIVEN
            when(userService.getUserByIdWithBlankPasswd(anyInt())).thenThrow(new UnexpectedRollbackException("Error while getting user"));
            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiUserController.getUserById(1, request))
                    .getMessage()).isEqualTo("Error while getting user");
        }
    }

    @Nested
    @Tag("updateUser")
    @DisplayName("Tests for /api/user/update")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class UpdateUserTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("PUT");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/user/update");
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
            user.setPassword("apw1=Passwd");
            user.setFullname("AAA");
            user.setRole("USER");
        }

        @Test
        @Tag("ApiUserControllerTest")
        @DisplayName("test updateUser should return a Success ResponseEntity With Saved user")
        public void updateUserTestShouldReturnASuccessResponseEntityWithSavedUser() {

            //GIVEN
            Optional<User> optionalUser = Optional.of(user);
            User userExpected = new User();
            userExpected.setId(1);
            userExpected.setUsername("Aaa");
            userExpected.setPassword("");
            userExpected.setFullname("AAA");
            userExpected.setRole("USER");
            when(userService.saveUser(any(User.class))).thenReturn(userExpected);

            //WHEN
            ResponseEntity<User> responseEntity = null;
            try {
                responseEntity = apiUserController.updateUser(optionalUser, request);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //THEN
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            User resultUser = responseEntity.getBody();
            assertThat(resultUser).isNotNull();
            assertThat(resultUser)
                    .extracting(
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
                            "USER"
                    );
        }

        @Test
        @Tag("ApiUserControllerTest")
        @DisplayName("test updateUser should throw BadRequestException")
        public void updateUserTestShouldThrowBadRequestException() {

            //GIVEN
            Optional<User> optionalUser = Optional.empty();

            //WHEN
            //THEN
            assertThat(assertThrows(BadRequestException.class,
                    () -> apiUserController.updateUser(optionalUser, request))
                    .getMessage()).isEqualTo("Correct request should be a json User body");
        }

        @Test
        @Tag("ApiUserControllerTest")
        @DisplayName("test updateUser should throw UnexpectedRollbackException")
        public void updateUserTestShouldThrowMUnexpectedRollbackException() {

            //GIVEN
            Optional<User> optionalUser = Optional.of(user);
            when(userService.saveUser(any(User.class))).thenThrow(new UnexpectedRollbackException("Error while saving user"));

            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiUserController.updateUser(optionalUser, request))
                    .getMessage()).isEqualTo("Error while saving user");
        }
    }

    @Nested
    @Tag("deleteByIdTests")
    @DisplayName("Tests for /api/user/delete/{id}")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class DeleteByIdTests {

        @BeforeAll
        public void setUpForAllTests() {
            requestMock = new MockHttpServletRequest();
            requestMock.setMethod("DELETE");
            requestMock.setServerName("http://localhost:8080");
            requestMock.setRequestURI("/api/user/delete/1");
            request = new ServletWebRequest(requestMock);
        }

        @AfterAll
        public void unSetForAllTests() {
            requestMock = null;
            request = null;
        }

        @Test
        @Tag("ApiUserControllerTest")
        @DisplayName("test deleteById should return HttpStatus.OK")
        public void deleteByIdTestShouldReturnHttpStatusOK() {

            //GIVEN
            //WHEN
            HttpStatus httpStatus = apiUserController.deleteUserById(1, request);

            //THEN
            assertThat(httpStatus.is2xxSuccessful()).isTrue();
        }

        @Test
        @Tag("ApiUserControllerTest")
        @DisplayName("test deleteById should throw UnexpectedRollbackException")
        public void deleteByIdTestShouldThrowUnexpectedRollbackException() {

            //GIVEN
            doThrow(new UnexpectedRollbackException("Error while deleting user")).when(userService).deleteUserById(anyInt());
            //WHEN
            //THEN
            assertThat(assertThrows(UnexpectedRollbackException.class,
                    () -> apiUserController.deleteUserById(1, request))
                    .getMessage()).isEqualTo("Error while deleting user");
        }
    }
}
