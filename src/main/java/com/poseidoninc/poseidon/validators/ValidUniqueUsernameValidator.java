package com.poseidoninc.poseidon.validators;

import com.poseidoninc.poseidon.annotations.ValidUniqueUsername;
import com.poseidoninc.poseidon.domain.User;
import com.poseidoninc.poseidon.repositories.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class ValidUniqueUsernameValidator implements ConstraintValidator<ValidUniqueUsername, User> {

    private final UserRepository userRepository;
    @Override
    public boolean isValid(User user, ConstraintValidatorContext context) {
        return true;
    }
}
