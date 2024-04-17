package com.poseidoninc.poseidon.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(Lifecycle.PER_CLASS)
public class UserTU {

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
    @ValueSource(strings = {"         ", "ap 1=Passwd", "1=Passw", "apw1=asswd", "apw1Passwd", "apw=Passwd", "apw1=Passwdapw1=Passwd"})
    @Tag("UserTU")
    @DisplayName("@Valid test with incorrect password should throw a ConstraintViolationException")
    public void saveTestIncorrectPasswdShouldThrowAConstraintViolationException(String passwd) {

        //GIVEN
        PasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();
        user.setId(null);
        user.setUsername("Aaa");
        user.setPassword(passwd);
        user.setFullname("AAA");
        user.setRole("USER");
        String msg = passwd==null? "Password is mandatory":null;

        //WHEN
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);

        //THEN
        if (passwd != null) {
            assertThat(passwordEncoder.matches(passwd, "")).isFalse();
        }
        assertThat(constraintViolations).isNotEmpty();
        if (passwd == null) {
            assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo(msg);
        }
    }

    @ParameterizedTest(name = "{0} should throw a ConstraintViolationException")
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "AbcdefghijklmnopqrstuvwxyzAbcdefghijklmnopqrstuvwxyzAbcdefghijklmnopqrstuvwxyzAbcdefghijklmnopqrstuvwxyzAbcdefghijklmnopqrstuvwxyz"})//26*5=130
    @Tag("UserTU")
    @DisplayName("@Valid test with a incorrect username should throw a ConstraintViolationException")
    public void saveTestIncorrectUsernameShouldThrowAConstraintViolationException(String username) {

        //GIVEN
        user.setId(null);
        user.setUsername(username);
        user.setPassword("apw1=Passwd");
        user.setFullname("AAA");
        user.setRole("USER");
        String msg = null;
        if (username!=null) {
            if (!username.isBlank()) {
                msg = "Username must be maximum of 125 characters";//26*5=130
            } else {
                msg = "Username is mandatory"; //empty or blank
            }
        } else {
            msg = "Username is mandatory"; //null
        }

        //WHEN
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);

        //THEN
        assertThat(constraintViolations).isNotEmpty();
    }
}

