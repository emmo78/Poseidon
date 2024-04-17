package com.poseidoninc.poseidon.validator;

import com.poseidoninc.poseidon.annotation.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.*;

import java.util.Arrays;
import java.util.List;


/**
 * The ValidPasswordValidator class is an implementation of the ConstraintValidator interface that validates a password
 * based on specified rules.
 */
public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {

    /**
     * Determines whether a given password is valid based on specified rules.
     *
     * @param password The password to validate.
     * @param context  The constraint validator context.
     * @return True if the password is valid, false otherwise.
     */
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        PasswordValidator passwordValidator = new PasswordValidator(Arrays.asList(
                // at least 8 characters, max 13
                // need spring.jpa.properties.jakarta.persistence.validation.mode=none in application.properties
                // because once encoded length will be >  13 and will throw violation on persistence
                new LengthRule(8, 13),
                // at least one upper-case character
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                // at least one lower-case character
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                // at least one digit character
                new CharacterRule(EnglishCharacterData.Digit, 1),
                // at least one symbol (special character)
                new CharacterRule(EnglishCharacterData.Special, 1),
                // no whitespace
                new WhitespaceRule(),
                // rejects passwords that contain a sequence of >= 3 repeating characters
                new RepeatCharactersRule(3),
                // rejects passwords that contain a sequence of >= 5 characters alphabetical  (e.g. abcdef)
                new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 5, false),
                // rejects passwords that contain a sequence of >= 5 characters numerical   (e.g. 12345)
                new IllegalSequenceRule(EnglishSequenceData.Numerical, 5, false)
        ));

        RuleResult result = passwordValidator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        List<String> messages = passwordValidator.getMessages(result);

        String messageTemplate = String.join(",", messages);
        context.buildConstraintViolationWithTemplate(messageTemplate)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
        return false;
    }
}
