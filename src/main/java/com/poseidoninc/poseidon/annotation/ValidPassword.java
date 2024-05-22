package com.poseidoninc.poseidon.annotation;

import com.poseidoninc.poseidon.validator.ValidPasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * annotation used to mark password field needs to be validated.
 * validation performed by
 * @see ValidPasswordValidator
 *
 * @author olivier morel
 */
@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = ValidPasswordValidator.class)
@Documented
public @interface ValidPassword {

    String message() default "Invalid Password";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}