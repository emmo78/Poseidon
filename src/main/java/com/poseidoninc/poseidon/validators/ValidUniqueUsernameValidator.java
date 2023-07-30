package com.poseidoninc.poseidon.validators;

import com.poseidoninc.poseidon.annotations.ValidUniqueUsername;
import com.poseidoninc.poseidon.domain.User;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidUniqueUsernameValidator implements ConstraintValidator<ValidUniqueUsername, User> {

    @Override
    public boolean isValid(User user, ConstraintValidatorContext context) {
        return true;
    }
}
