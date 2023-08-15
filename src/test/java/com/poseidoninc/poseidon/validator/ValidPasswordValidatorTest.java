package com.poseidoninc.poseidon.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ValidPasswordValidatorTest {

    @InjectMocks
    private ValidPasswordValidator validPasswordValidator;

    @Mock
    ConstraintValidatorContext constraintValidatorContext;
    @Mock
    ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder;

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
        String password = "apw1=Passwd";

        //THEN
        assertThat(validPasswordValidator.isValid(password, constraintValidatorContext)).isTrue();
    }

    @ParameterizedTest(name = "{0} causes isValid should return false")
    @EmptySource
    @ValueSource(strings = {"         ", "ap 1=Passwd","1=Passw", "apw1=asswd", "apw1Passwd", "apw=Passwd", "apw1=Passwdapw1=Passwd"})
    @Tag("ValidPasswordValidatorTests")
    @DisplayName("isValid Test should return false")
    public void isValidTestShouldReturnFalse(String password) {

        //GIVEN
        when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(constraintViolationBuilder);
        when(constraintViolationBuilder.addConstraintViolation())
                .thenReturn(constraintValidatorContext);
        //WHEN
        //THEN
        assertThat(validPasswordValidator.isValid(password, constraintValidatorContext)).isFalse();
    }
}