package com.poseidoninc.poseidon.validator;

import lombok.RequiredArgsConstructor;
import org.passay.*;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

@Component
public class ValidPasswordGenerator {

    public String generatePassword() {
        PasswordGenerator passwordGenerator = new PasswordGenerator(new SecureRandom());
        String password;
        PasswordValidator passwordValidator = new PasswordValidator();
        RuleResult result;
        do {
            password = passwordGenerator.generatePassword(13, List.of(
                    // at least one upper-case character
                    new CharacterRule(EnglishCharacterData.UpperCase, 1),
                    // at least one lower-case character
                    new CharacterRule(EnglishCharacterData.LowerCase, 1),
                    // at least one digit character
                    new CharacterRule(EnglishCharacterData.Digit, 1),
                    // at least one symbol (special character)
                    new CharacterRule(EnglishCharacterData.Special, 1)
            ));
            result = passwordValidator.validate(new PasswordData(password));
        } while (!result.isValid());
        return password;
    }
}
