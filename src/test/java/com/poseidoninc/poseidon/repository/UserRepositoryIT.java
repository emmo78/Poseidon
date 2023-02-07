package com.poseidoninc.poseidon.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.poseidoninc.poseidon.domain.User;
import com.poseidoninc.poseidon.repositories.UserRepository;

import jakarta.validation.ConstraintViolationException;

@SpringBootTest
public class UserRepositoryIT {
	
	@Autowired
	private UserRepository userRepository;

	private User user;
	
	@BeforeEach
	public void setUpPerTest() {
		user = new User();
	}
	
	@AfterEach
	public void undefPerTest() {
		userRepository.deleteAll();
		user = null;
	}
	
	@Nested
	@Tag("saveUserTests")
	@DisplayName("Tests for validation and saving user")
	@TestInstance(Lifecycle.PER_CLASS)
	class saveUserTests {

		@Test
		@Tag("UserRepositoryIT")
		@DisplayName("save test should persist user with new id")
		public void saveShouldPersistUserWithNewId() {
	
			//GIVEN
			user.setId(null);
			user.setUsername("Aaa");
			user.setPassword("aaa1=Passwd");
			user.setFullname("AAA");
			user.setRole("USER");
	
			//WHEN
			User userResult = userRepository.saveAndFlush(user);
			
			//THEN
			Optional<Integer> idOpt = Optional.ofNullable(userResult.getId());
			assertThat(idOpt).isPresent();
			idOpt.ifPresent(id -> assertThat(userRepository.findById(id)).get().extracting(
					User::getUsername,
					User::getPassword,
					User::getFullname,
					User::getRole)
				.containsExactly(
					"Aaa",
					"aaa1=Passwd",
					"AAA",
					"USER"));
		}
		
		@ParameterizedTest(name = "{0} should throw a ConstraintViolationException")
		@ValueSource(strings = {"1=Passw", "aaa1=asswd", "aaa1Passwd", "aaa=Passwd"})
		@Tag("UserRepositoryIT")
		@DisplayName("save test with incorrect password should throw a ConstraintViolationException")
		public void saveShouldThrowAConstraintViolationException(String passwd) {
	
			//GIVEN
			user.setId(null);
			user.setUsername("Aaa");
			user.setPassword(passwd);
			user.setFullname("AAA");
			user.setRole("USER");
	
			//WHEN
			//THEN
			assertThat(assertThrows(ConstraintViolationException.class,
					() -> userRepository.saveAndFlush(user))
					.getMessage()).contains("Password must have at least 8 characters in length, max 13, containing at least 1 uppercase letter, 1 digit, and 1 symbol.");
		}
	}
	
	@Nested
	@Tag("findByUsernameTests")
	@DisplayName("Tests finding user by username")
	@TestInstance(Lifecycle.PER_CLASS)
	class FindByUserNameTests {

		@Test
		@Tag("UserRepositoryIT")
		@DisplayName("find by Username should return the user by Username")
		public void findByUsernameTestShouldReturnUserByUsername() {
	
			//GIVEN
			user.setId(null);
			user.setUsername("Aaa");
			user.setPassword("aaa1=Passwd");
			user.setFullname("AAA");
			user.setRole("USER");
			userRepository.saveAndFlush(user);
	
			//WHEN
			User userResult = userRepository.findByUsername("Aaa");
			
			//THEN
			assertThat(userResult).extracting(
					User::getUsername,
					User::getPassword,
					User::getFullname,
					User::getRole)
				.containsExactly(
					"Aaa",
					"aaa1=Passwd",
					"AAA",
					"USER");	
		}
		
		@Test
		@Tag("UserRepositoryIT")
		@DisplayName("find by incorrect username should return null")
		public void findByIncorrectUsernameTestShouldReturnNull() {
	
			//GIVEN
			user.setId(null);
			user.setUsername("Aaa");
			user.setPassword("aaa1=Passwd");
			user.setFullname("AAA");
			user.setRole("USER");
			userRepository.saveAndFlush(user);
	
			//WHEN
			User userResult = userRepository.findByUsername("Bbb");
			
			//THEN
			assertThat(userResult).isNull();
			
		}
		
		@Test
		@Tag("UserRepositoryIT")
		@DisplayName("find by null username should return null")
		public void findByNullUserNameTestShouldReturnNull() {
	
			//GIVEN
			user.setId(null);
			user.setUsername("Aaa");
			user.setPassword("aaa1=Passwd");
			user.setFullname("AAA");
			user.setRole("USER");
			userRepository.saveAndFlush(user);
	
			//WHEN
			User userResult = userRepository.findByUsername(null);
			
			//THEN
			assertThat(userResult).isNull();	
		}
	}
	
	@Nested
	@Tag("existsByUsernameTests")
	@DisplayName("Tests exists by userName")
	@TestInstance(Lifecycle.PER_CLASS)
	class ExistsByUserNameTests {

		@Test
		@Tag("UserRepositoryIT")
		@DisplayName("exists by Username should return true")
		public void findByUsernameTestShouldReturnTrue() {
	
			//GIVEN
			user.setId(null);
			user.setUsername("Aaa");
			user.setPassword("aaa1=Passwd");
			user.setFullname("AAA");
			user.setRole("USER");
			userRepository.saveAndFlush(user);
	
			//WHEN
			boolean result = userRepository.existsByUsername("Aaa");
			
			//THEN
			assertThat(result).isTrue();	
		}
		
		@Test
		@Tag("UserRepositoryIT")
		@DisplayName("exists by Username should return false")
		public void findByUsernameTestShouldReturnFalse() {
	
			//GIVEN
			user.setId(null);
			user.setUsername("Aaa");
			user.setPassword("aaa1=Passwd");
			user.setFullname("AAA");
			user.setRole("USER");
			userRepository.saveAndFlush(user);
	
			//WHEN
			boolean result = userRepository.existsByUsername("Bbb");
			
			//THEN
			assertThat(result).isFalse();	
		}
	}
}