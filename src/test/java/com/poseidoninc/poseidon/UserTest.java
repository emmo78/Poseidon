package com.poseidoninc.poseidon;

import com.poseidoninc.poseidon.domain.User;
import com.poseidoninc.poseidon.repositories.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@TestInstance(Lifecycle.PER_CLASS)
public class UserTest {

    private ValidatorFactory factory;
    private Validator validator;
    private User user;

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

    @Nested
    @SpringBootTest
    @ActiveProfiles("mytest")
    @Tag("validUsernameIT")
    @DisplayName("Tests validation for unique username")
    @TestInstance(Lifecycle.PER_CLASS)
    class validUniqueUsernameTests {

        @Autowired
        private UserRepository userRepository;

        private Integer id;

        private User userTest;

        //@BeforeAll
        //public void setUpForAllNestedValidTests() {}

        //@AfterAll
        //public void undefForAllNestedValidTests() {}

        @BeforeEach
        public void setUpForEachTests() {
            user = new User();
            user.setUsername("Aaa");
            user.setPassword("aaa1=Passwd");
            user.setFullname("AAA");
            user.setRole("USER");
            id = userRepository.saveAndFlush(user).getId();
        }

        @AfterEach
        public void undefForEachTests() {
            userRepository.deleteAll();
            user = null;
            userTest = null;
            id = null;
        }

        @ParameterizedTest(name = "id = {0}, userTestName = {1}, Valid UserTest should not returns ConstraintViolation object")
        @MethodSource
        @Tag("UserTest")
        @DisplayName("Test @Valid should valid unique username ignoring case ")
        public void validUsernameTestShouldBeValid(Integer idTest, String userTestName) {

            //GIVEN
            userTest = new User();
            userTest.setId(idTest);
            userTest.setUsername(userTestName);
            userTest.setPassword("bbb1=Passwd");
            userTest.setFullname("BBB");
            userTest.setRole("USER");

            //WHEN
            Set<ConstraintViolation<User>> constraintViolations = validator.validate(userTest);

            //THEN
            assertThat(constraintViolations).isEmpty();
        }

        Stream<Arguments> validUsernameTestShouldBeValid() {
            return Stream.of(
                    arguments(null, "Bbb"), // save new user not already in database
                    arguments(id, "Aaa"), // update user already in data base
                    arguments(id, "Bbb") // user update username not existing yet in database
            );
        }

    }

}
