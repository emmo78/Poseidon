package com.poseidoninc.poseidon.validator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

public class ValidPasswordValidatorTest {

    private ValidPasswordValidator validPasswordValidator;

    @BeforeEach
    public void setUpPerTest() {
        validPasswordValidator = new ValidPasswordValidator();
    }

    @AfterEach
    public void undefPerTest() {
        validPasswordValidator = null;
    }

    @Test
    @Tag("ValidPasswordValidatorTests")
    @DisplayName("isValid Test should return true")
    public void isValidTestShouldReturnTrue() {

        //GIVEN
        String password = "aaa1=Passwd";

        //WHEN
        //THEN
        assertThat(validPasswordValidator.isValid(password, null)).isTrue();
    }

    @ParameterizedTest(name = "{0} should throw a ConstraintViolationException")
    @NullAndEmptySource
    @ValueSource(strings = {"1=Passw", "aaa1=asswd", "aaa1Passwd", "aaa=Passwd", "aaa1=Passwdaaa1=Passwd"})
    @Tag("ValidPasswordValidatorTests")
    @DisplayName("isValid Test should return false")
    public void isValidTestShouldReturnFalse(String password) {

        //GIVEN
        //WHEN
        //THEN
        assertThat(validPasswordValidator.isValid(password, null)).isFalse();
    }
}