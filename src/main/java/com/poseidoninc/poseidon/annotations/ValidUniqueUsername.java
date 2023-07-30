package com.poseidoninc.poseidon.annotations;


import com.poseidoninc.poseidon.validators.ValidUniqueUsernameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = { ValidUniqueUsernameValidator.class })
@Documented
public @interface ValidUniqueUsername {
    String message() default "{UserName already exists}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
