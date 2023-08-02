package com.poseidoninc.poseidon;

import com.poseidoninc.poseidon.domain.User;
import com.poseidoninc.poseidon.repositories.UserRepository;
import jakarta.validation.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@TestInstance(Lifecycle.PER_CLASS)
public class UserTest {

    private ValidatorFactory factory;

    // private Validator validator;

    private User userA;

    private User userB;

    @BeforeAll
    public void setUpForAllTests() {
        //factory = Validation.buildDefaultValidatorFactory();
        //validator = factory.getValidator();
    }

    @AfterAll
    public void undefForAllTests() {
        //validator = null;
        //factory = null;
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

        @Autowired
        private Validator validator;
        private User userTest;

        @BeforeEach
        public void setUpForEachTests() {
            userA = new User();
            userA.setUsername("Aaa");
            userA.setPassword("aaa1=Passwd");
            userA.setFullname("AAA");
            userA.setRole("USER");
            userRepository.saveAndFlush(userA);
        }

        @AfterEach
        public void undefForEachTests() {
            userRepository.deleteAll();
            userA = null;
            userB = null;
            userTest = null;
        }

        @ParameterizedTest(name = "id = {0}, userTestName = {1}, Valid UserTest should not returns ConstraintViolation object")
        @CsvSource(value = {"null, Bbb", // new user not already in data base
                "1, Aaa", // user already in data base update
                "1, Bbb"} // user update user name not existing in data base
                ,nullValues = {"null"})
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

        @ParameterizedTest(name = "id = {0}, userTestName = {1}, Valid UserTest should return ConstraintViolation object")
        @CsvSource(value = {"null, Aaa", // new user with user name already in data base
                "2, Aaa", // user changed his name to an existing one in the database
                "3, Ccc"} // user update not existing in database
                ,nullValues = {"null"})
        @Tag("UserTest")
        @DisplayName("Test @Valid should not valid unique username ignoring case ")
        public void validUsernameTestShouldNotBeValid(Integer idTest, String userTestName) {

            //GIVEN
            userB = new User();
            userB.setId(2);
            userB.setUsername("Bbb");
            userB.setPassword("bbb1=Passwd");
            userB.setFullname("BBB");
            userB.setRole("USER");
            userRepository.saveAndFlush(userB);

            userTest = new User();
            userTest.setId(idTest);
            userTest.setUsername(userTestName);
            userTest.setPassword("ccc1=Passwd");
            userTest.setFullname("CCC");
            userTest.setRole("USER");

            //WHEN
            Set<ConstraintViolation<User>> constraintViolations = validator.validate(userTest);

            //THEN
            assertThat(constraintViolations).hasSize(1);
            assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo("UserName already exists");
        }
    }
}
