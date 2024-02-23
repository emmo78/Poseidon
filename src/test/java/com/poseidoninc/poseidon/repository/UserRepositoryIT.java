package com.poseidoninc.poseidon.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.poseidoninc.poseidon.domain.User;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
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
			user.setPassword("apw1=Passwd");
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
							"apw1=Passwd",
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
			user.setPassword("apw1=Passwd");
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
		@ValueSource(strings = {"Aaa", "aAa", "aaA", "aAA", "AaA", "AAa", "AAA", "aaa"})
		@Tag("UserRepositoryIT")
		@DisplayName("save test an new user with an existent username case insensitive should throw a DataIntegrityViolationException")
		public void saveTestAnUserWithAnExistentUsernameCaseInsensitiveShouldThrowDataIntegrityViolationException(String username) {

			//GIVEN
			user.setId(null);
			user.setUsername("Aaa");
			user.setPassword("apw1=Passwd");
			user.setFullname("AAA");
			user.setRole("USER");
			userRepository.saveAndFlush(user);

			User userTest = new User();
			userTest.setId(null);
			userTest.setUsername(username);
			userTest.setPassword("apw2=Passwd");
			userTest.setFullname("AAATEST");
			userTest.setRole("USER");

			//WHEN
			//THEN
			assertThat(assertThrows(DataIntegrityViolationException.class,
					() -> userRepository.saveAndFlush(userTest))
					.getMessage()).contains(("Unique index or primary key violation"));
		}
	}

	@Nested
	@Tag("findByUsernameTests")
	@DisplayName("Tests finding user by username")
	@TestInstance(Lifecycle.PER_CLASS)
	class FindByUserNameTests {

		@ParameterizedTest(name = "{0} should throw a DataIntegrityViolationException")
		@ValueSource(strings = {"Aaa", "aAa", "aaA", "aAA", "AaA", "AAa", "AAA", "aaa"})
		@Tag("UserRepositoryIT")
		@DisplayName("find by Username should return the user by Username case insensitive")
		public void findByUsernameTestShouldReturnUserByUsernameCaseInsensitive(String username) {
	
			//GIVEN
			user.setId(null);
			user.setUsername("Aaa");
			user.setPassword("apw1=Passwd");
			user.setFullname("AAA");
			user.setRole("USER");
			userRepository.saveAndFlush(user);
	
			//WHEN
			User userResult = userRepository.findByUsername(username);
			
			//THEN
			assertThat(userResult).extracting(
					User::getUsername,
					User::getPassword,
					User::getFullname,
					User::getRole)
				.containsExactly(
					"Aaa",
					"apw1=Passwd",
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
			user.setPassword("apw1=Passwd");
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
			user.setPassword("apw1=Passwd");
			user.setFullname("AAA");
			user.setRole("USER");
			userRepository.saveAndFlush(user);
	
			//WHEN
			User userResult = assertDoesNotThrow(() -> userRepository.findByUsername(null));
			
			//THEN
			assertThat(userResult).isNull();	
		}
	}
	@Test
	@Tag("UserRepositoryIT")
	@DisplayName("find by Id null should throw an InvalidDataAccessApiUsageException")
	public void findByIdNullShouldThrowAnInvalidDataAccessApiUsageException() {
		//GIVEN
		//WHEN
		//THEN
		assertThat(assertThrows(InvalidDataAccessApiUsageException.class, () -> userRepository.findById(null)).getMessage())
				.contains("The given id must not be null");
	}

	@Test
	@Tag("UserReositoyIT")
	@DisplayName("delete null should throw an InvalidDataAccessApiUsageException")
	public void deleteNullShouldThrowAnInvalidDataAccessApiUsageException() {
		//GIVEN
		User user = null;
		//WHEN
		//THEN
		assertThat(assertThrows(InvalidDataAccessApiUsageException.class, () -> userRepository.delete(user)).getMessage())
				.contains("Entity must not be null");
	}
}