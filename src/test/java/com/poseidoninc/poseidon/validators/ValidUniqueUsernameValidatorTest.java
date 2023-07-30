package com.poseidoninc.poseidon.validators;

import com.poseidoninc.poseidon.domain.User;
import com.poseidoninc.poseidon.repositories.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ValidUniqueUsernameValidatorTest {

    @InjectMocks
    ValidUniqueUsernameValidator validUniqueUsernameValidator;

    @Mock
    UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUpForEachTest() {
        user = new User(); // user already in data base
        user.setId(1);
        user.setUsername("Aaa");
        user.setPassword("aaa1=Passwd");
        user.setFullname("AAA");
        user.setRole("USER");
    }

    @AfterEach
    public void unSetForEachTests() {
        user = null;
    }

    @ParameterizedTest(name = "id = {0}, userToSaveName = {1}, existsByUserName return {2}, isValid should return true")
    @CsvSource(value = {"null, Aaa, false", // save new user not already in data base
            "1, Aaa, true", // update user already in data base
            "1, Bbb, false"} // user update user name not existing yet in data base
            ,nullValues = {"null"})    @Tag("UsernameValidatorTest")
    @DisplayName("Test isValid should return True")
    public void isValidTestShouldReturnTrue(Integer id, String userToSaveName, boolean existsByUserName) {

        //GIVEN
        User userToSave = new User();
        userToSave.setId(id);
        userToSave.setUsername(userToSaveName);
        userToSave.setPassword("aaa1=Passwd");
        userToSave.setFullname("AAA");
        userToSave.setRole("USER");

        when(userRepository.findById(nullable(Integer.class))).then(invocation -> {
            Integer index = invocation.getArgument(0);
            if (index == null) {
                return Optional.empty();
            } else {
                return Optional.of(user);
            }
        });
        when(userRepository.existsByUsername(anyString())).thenReturn(existsByUserName);

        //WHEN
        //THEN
        assertThat(validUniqueUsernameValidator.isValid(userToSave, null)).isTrue();


    }
}