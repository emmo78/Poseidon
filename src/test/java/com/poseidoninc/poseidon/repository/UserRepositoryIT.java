package com.poseidoninc.poseidon.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.poseidoninc.poseidon.domain.User;
import com.poseidoninc.poseidon.repositories.UserRepository;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("mytest")
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
	class saveUserTests {

		@Test
		@Tag("UserRepositoryIT")
		@DisplayName("save test new user should persist user with new id")
		public void saveTestShouldPersistUserWithNewId() {
	
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

		@Test
		@Tag("UserRepositoryIT")
		@DisplayName("save test update user with same username should persist him")
		public void saveTestUpdateUserShouldPersistHim() {

			//GIVEN
			user.setId(null);
			user.setUsername("Aaa");
			user.setPassword("aaa1=Passwd");
			user.setFullname("AAA");
			user.setRole("USER");
			Integer id = userRepository.saveAndFlush(user).getId();
			User updatedUser = new User();
			updatedUser.setId(id);
			updatedUser.setUsername("Aaa");
			updatedUser.setPassword("upd1=Passwd");
			updatedUser.setFullname("UPDT");
			updatedUser.setRole("USER");

			//WHEN
			//THEN
			assertThat(assertDoesNotThrow(() -> userRepository.saveAndFlush(updatedUser)))
					.extracting(
							User::getId,
							User::getUsername,
							User::getPassword,
							User::getFullname,
							User::getRole)
					.containsExactly(
							id,
							"Aaa",
							"upd1=Passwd",
							"UPDT",
							"USER");
		}
		@ParameterizedTest(name = "{0} should throw a DataIntegrityViolationException")
		@ValueSource(strings = {"Aaa", "aaa", "AAA", "aAA"})
		@Tag("UserRepositoryIT")
		@DisplayName("save test an new user with an existent username case insensitive should throw a DataIntegrityViolationException")
		public void saveTestAnUserWithAnExistentUsernameCaseInsensitiveShouldThrowDataIntegrityViolationException(String username) {

			//GIVEN
			user.setId(null);
			user.setUsername("Aaa");
			user.setPassword("aaa1=Passwd");
			user.setFullname("AAA");
			user.setRole("USER");
			userRepository.saveAndFlush(user);

			User userTest = new User();
			userTest.setId(null);
			userTest.setUsername(username);
			userTest.setPassword("aaa2=Passwd");
			userTest.setFullname("AAATEST");
			userTest.setRole("USER");

			//WHEN
			//THEN
			assertThat(assertThrows(DataIntegrityViolationException.class,
					() -> userRepository.saveAndFlush(userTest))
					.getMessage()).contains(("Unique index or primary key violation"));
		}


		@ParameterizedTest(name = "{0} should throw a ConstraintViolationException")
		@NullAndEmptySource
		@ValueSource(strings = {"1=Passw", "aaa1=asswd", "aaa1Passwd", "aaa=Passwd", "aaa1=Passwdaaa1=Passwd"})
		@Tag("UserRepositoryIT")
		@DisplayName("save test with incorrect password should throw a ConstraintViolationException")
		public void saveTestIncorrectPasswdShouldThrowAConstraintViolationException(String passwd) {
	
			//GIVEN
			user.setId(null);
			user.setUsername("Aaa");
			user.setPassword(passwd);
			user.setFullname("AAA");
			user.setRole("USER");
			String msg = passwd==null? "Password is mandatory":"Password must have at least 8 characters in length, max 13, containing at least 1 uppercase letter, 1 digit, and 1 symbol.";

			//WHEN
			//THEN
			assertThat(assertThrows(ConstraintViolationException.class,
					() -> userRepository.saveAndFlush(user))
					.getMessage()).contains(msg);
		}
		
		@ParameterizedTest(name = "{0} should throw a ConstraintViolationException")
		@NullAndEmptySource
		@ValueSource(strings = {"   ", "AbcdefghijklmnopqrstuvwxyzAbcdefghijklmnopqrstuvwxyzAbcdefghijklmnopqrstuvwxyzAbcdefghijklmnopqrstuvwxyzAbcdefghijklmnopqrstuvwxyz"})//26*5=130
		@Tag("UserRepositoryIT")
		@DisplayName("save test with a incorrect String should throw a ConstraintViolationException")
		public void saveTestIncorrectStringShouldThrowAConstraintViolationException(String username) {
	
			//GIVEN
			user.setId(null);
			user.setUsername(username);
			user.setPassword("aaa1=Passwd");
			user.setFullname("AAA");
			user.setRole("USER");
			String msg = null;
			if (!(username==null)) {
				if (!username.isBlank()) {
					msg = "Username must be maximum of 125 characters";//26*5=130
				} else {
					msg = "Username is mandatory"; //empty or blank
				}
			} else {
				msg = "Username is mandatory"; //null
			}
	
			//WHEN
			//THEN
			assertThat(assertThrows(ConstraintViolationException.class,
					() -> userRepository.saveAndFlush(user))
					.getMessage()).contains(msg);
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

	//InvalidDataAccessApiUsageException
}