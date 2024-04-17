package com.poseidoninc.poseidon.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CurvePointTU {

    private ValidatorFactory factory;

    private Validator validator;

    private CurvePoint curvePoint;

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
        curvePoint = new CurvePoint();
    }

    @AfterEach
    public void undefPerTest() {
        curvePoint = null;
    }

    @ParameterizedTest(name = "{0} should throw a ConstraintViolationException")
    @CsvSource(value = {"null, must not be null",
            "-1, CurveId min is 1",
            "0, CurveId min is 1",
            "128, CurveId is a tinyint so max is 127"},
            nullValues = {"null"})
    @Tag("CurvePointRepositoryIT")
    @DisplayName("save test with incorrect curveId should throw a ConstraintViolationException")
    public void saveTestIncorrectPasswdShouldThrowAConstraintViolationException(String curveIdS, String msg) {

        //GIVEN

        Integer curveId = curveIdS == null ? null : Integer.parseInt(curveIdS);
        curvePoint.setId(null);
        curvePoint.setCurveId(curveId);
        curvePoint.setAsOfDate(LocalDateTime.parse("21/01/2023 10:20:30", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        curvePoint.setTerm(3.0);
        curvePoint.setValue(4.0);
        curvePoint.setCreationDate(LocalDateTime.parse("22/01/2023 12:22:32", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

        //WHEN
        Set<ConstraintViolation<CurvePoint>> constraintViolations = validator.validate(curvePoint);

        //THEN
        assertThat(constraintViolations).isNotEmpty();
    }


}
