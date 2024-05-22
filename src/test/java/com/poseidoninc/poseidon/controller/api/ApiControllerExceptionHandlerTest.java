package com.poseidoninc.poseidon.controller.api;

import com.poseidoninc.poseidon.domain.User;
import com.poseidoninc.poseidon.error.ApiError;
import com.poseidoninc.poseidon.exception.BadRequestException;
import com.poseidoninc.poseidon.service.RequestService;
import com.poseidoninc.poseidon.service.RequestServiceImpl;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.metadata.ConstraintDescriptor;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * unit test class for the ApiControllerExceptionHandler.
 * @author olivier morel
 */
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApiControllerExceptionHandlerTest {
    @InjectMocks
    private ApiControllerExceptionHandler apiControllerExceptionHandler;

    @Spy
    private final RequestService requestService = new RequestServiceImpl();

    private MockHttpServletRequest requestMock;
    private WebRequest request;

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

    @AfterEach
    public void unsetForEachTest() {
        apiControllerExceptionHandler = null;
   }

    @Test
    @Tag("ApiControllerExceptionHandlerTest")
    @DisplayName("test methodArgumentNotValidException should return a Bad Request ResponseEntity")
    public void methodArgumentNotValidException() {

        //GIVEN
        Method method = null;
        try {
            method = ApiUserController.class.getDeclaredMethod("createUser", Optional.class, WebRequest.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        // index of the parameter @RequestBody Optional<@Valid User> optionalUser
        MethodParameter methodParameter = new MethodParameter(method, 0);

        // user to be validated
        Object user = User.class;
        DirectFieldBindingResult bindingResult = new DirectFieldBindingResult(user, "User");
        bindingResult.addError(new FieldError("User", "password",  "Password is mandatory"));

        MethodArgumentNotValidException manve =  new MethodArgumentNotValidException(methodParameter, bindingResult);

        //WHEN
        ResponseEntity<ApiError> responseEntity = apiControllerExceptionHandler.methodArgumentNotValidException(manve, request);
        //THEN
        assertThat(responseEntity.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST)).isTrue();
        //Validation failed for argument [0] in public org.springframework.http.ResponseEntity<com.poseidoninc.poseidon.domain.User> com.poseidoninc.poseidon.controller.api.ApiUserController.createUser(java.util.Optional<com.poseidoninc.poseidon.domain.User>,org.springframework.web.context.request.WebRequest) throws org.springframework.web.bind.MethodArgumentNotValidException,com.poseidoninc.poseidon.exception.BadRequestException,org.springframework.dao.DataIntegrityViolationException,org.springframework.transaction.UnexpectedRollbackException: [Field error in object 'User' on field 'password': rejected value [null]; codes []; arguments []; default message [Password is mandatory]]
        assertThat(responseEntity.getBody().getMessage()).contains("Validation failed", "createUser", "'User'", "'password'", "[Password is mandatory]");
    }

    @Test
    @Tag("ApiControllerExceptionHandlerTest")
    @DisplayName("test badRequestException should return a Bad Request ResponseEntity")
    public void badRequestExceptionTest() {

        //GIVEN
        BadRequestException bre = new BadRequestException("Correct request should be a json Entity body");
        //WHEN
        ResponseEntity<ApiError> responseEntity = apiControllerExceptionHandler.badRequestException(bre, request);
        //THEN
        assertThat(responseEntity.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST)).isTrue();
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("Correct request should be a json Entity body");
    }

    @Test
    @Tag("ApiControllerExceptionHandlerTest")
    @DisplayName("test constraintViolationException should return a Bad Request ResponseEntity")
    public void constraintViolationExceptionTest() {

        //GIVEN
        // Class ConstraintViolationImpl is nested
        ConstraintViolationException cve = new ConstraintViolationException(Set.of(new ConstraintViolationImpl("Constraint violation")));
        //WHEN
        ResponseEntity<ApiError> responseEntity = apiControllerExceptionHandler.constraintViolationException(cve, request);
        //THEN
        assertThat(responseEntity.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST)).isTrue();
        /*
        jakarta.validation.ConstraintViolationException
        private static String toString(Set<? extends ConstraintViolation<?>> constraintViolations) {
		return constraintViolations.stream()
			.map( cv -> cv == null ? "null" : cv.getPropertyPath() + ": " + cv.getMessage() )
			.collect( Collectors.joining( ", " ) );

		given : cv == null -> so expected  "null"+": "+"Constraint violation"
         */
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("null: Constraint violation");
    }


    @Test
    @Tag("ApiControllerExceptionHandlerTest")
    @DisplayName("test dataIntegrityViolationException should return a Conflict ResponseEntity")
    public void dataIntegrityViolationExceptionTest() {

        //GIVEN
        DataIntegrityViolationException dive = new DataIntegrityViolationException("Error while...");
        //WHEN
        ResponseEntity<ApiError> responseEntity = apiControllerExceptionHandler.dataIntegrityViolationException(dive, request);
        //THEN
        assertThat(responseEntity.getStatusCode().isSameCodeAs(HttpStatus.CONFLICT)).isTrue();
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("Error while...");
    }


   @Test
    @Tag("ApiControllerExceptionHandlerTest")
    @DisplayName("test unexpectedRollbackException should return a Internal Server Error ResponseEntity")
    public void unexpectedRollbackExceptionTest() {

        //GIVEN
        UnexpectedRollbackException ure = new UnexpectedRollbackException("Error while...");
        //WHEN
        ResponseEntity<ApiError> responseEntity = apiControllerExceptionHandler.unexpectedRollbackException(ure, request);
        //THEN
        assertThat(responseEntity.getStatusCode().isSameCodeAs(HttpStatus.INTERNAL_SERVER_ERROR)).isTrue();
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("Error while...");
    }

    @Test
    @Tag("ApiControllerExceptionHandlerTest")
    @DisplayName("test unexpectedException should return a Internal Server Error ResponseEntity")
    public void unexpectedExceptionTest() {

        //GIVEN
        Exception e = new Exception("Unexpected Error...");
        //WHEN
        ResponseEntity<ApiError> responseEntity = apiControllerExceptionHandler.unexpectedException(e, request);
        //THEN
        assertThat(responseEntity.getStatusCode().isSameCodeAs(HttpStatus.INTERNAL_SERVER_ERROR)).isTrue();
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("Internal Server Error");
    }

    //Needed in constraintViolationExceptionTest()
    class ConstraintViolationImpl implements ConstraintViolation<Object> {

        private String message;

        public ConstraintViolationImpl(String message) {
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public String getMessageTemplate() {
            return "";
        }

        @Override
        public Object getRootBean() {
            return null;
        }

        @Override
        public Class<Object> getRootBeanClass() {
            return null;
        }

        @Override
        public Object getLeafBean() {
            return null;
        }

        @Override
        public Object[] getExecutableParameters() {
            return new Object[0];
        }

        @Override
        public Object getExecutableReturnValue() {
            return null;
        }

        @Override
        public Path getPropertyPath() {
            return null;
        }

        @Override
        public Object getInvalidValue() {
            return null;
        }

        @Override
        public ConstraintDescriptor<?> getConstraintDescriptor() {
            return null;
        }

        @Override
        public <U> U unwrap(Class<U> aClass) {
            return null;
        }
    }
}
