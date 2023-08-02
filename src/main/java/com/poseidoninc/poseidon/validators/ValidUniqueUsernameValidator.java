package com.poseidoninc.poseidon.validators;

import com.poseidoninc.poseidon.annotations.ValidUniqueUsername;
import com.poseidoninc.poseidon.domain.User;
import com.poseidoninc.poseidon.exception.ResourceConflictException;
import com.poseidoninc.poseidon.exception.ResourceNotFoundException;
import com.poseidoninc.poseidon.repositories.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

@Slf4j
public class ValidUniqueUsernameValidator implements ConstraintValidator<ValidUniqueUsername, User> {

    @Autowired
    private UserRepository userRepository;


    @Override
    public void initialize(ValidUniqueUsername constraintAnnotation) {
        //ConstraintValidator.super.initialize(constraintAnnotation);
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    @Override
    public boolean isValid(User user, ConstraintValidatorContext context) {
        Integer id = user.getId(); //can be null;
        String username = user.getUsername();
        String oldUsername = null;
        try {
            oldUsername = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found")).getUsername();
        } catch(IllegalArgumentException iae) {
            log.info("{} : {}", "validUniqueUsername", "userId is null");
        } catch(ResourceNotFoundException rnfe) {
            log.error("{} : user={} : {}", "validUniqueUsername", id, rnfe.toString());
            return false;
        } catch (Exception e) {
            log.error("{} : user={} : {}", "validUniqueUsername", id, e.toString());
            return false;
        }
        if ((id == null || !username.equalsIgnoreCase(oldUsername)) && userRepository.existsByUsername(username)) {
            log.error("{} : user={} : {}", "validUniqueUsername", username, "username conflict");
            return false;
        } else {
            log.info("{} : user={} : {}", "validUniqueUsername", username, "userId is valid");
            return true;
        }
   }
}
