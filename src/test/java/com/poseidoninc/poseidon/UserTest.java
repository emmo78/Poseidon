package com.poseidoninc.poseidon;

import com.poseidoninc.poseidon.domain.User;
import jakarta.validation.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(Lifecycle.PER_CLASS)
public class UserTest {

    private ValidatorFactory factory;

    private Validator validator;

    private User user;

    @BeforeAll
    public void setUpForAllTests() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    public void undefForAllTests() {
        validator = null;
        factory = null;
    }

    @BeforeEach
    public void setUpPerTest() {
        user = new User();
    }

    @AfterEach
    public void undefPerTest() {
        user = null;
    }

    @ParameterizedTest(name = "{0} should throw a ConstraintViolationException")
    @NullAndEmptySource
    @ValueSource(strings = {"         ", "ap 1=Passwd","1=Passw", "apw1=asswd", "apw1Passwd", "apw=Passwd", "apw1=Passwdapw1=Passwd"})
    @Tag("UserRepositoryIT")
    @DisplayName("@Valid test with incorrect password should throw a ConstraintViolationException")
    public void saveTestIncorrectPasswdShouldThrowAConstraintViolationException(String passwd) {

        //GIVEN
        user.setId(null);
        user.setUsername("Aaa");
        user.setPassword(passwd);
        user.setFullname("AAA");
        user.setRole("USER");
        String msg = passwd==null? "Password is mandatory":null;

        //WHEN
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);

        //THEN
        assertThat(constraintViolations).isNotEmpty();
        if (passwd == null) {
            assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo(msg);
        }
    }
}
