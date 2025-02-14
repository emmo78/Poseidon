package com.poseidoninc.poseidon.service;

import com.poseidoninc.poseidon.domain.User;
import com.poseidoninc.poseidon.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation class for UserService
 *
 * @see UserService
 *
 * @author olivier morel
 */
@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional(readOnly = true, rollbackFor = {ResourceNotFoundException.class, UnexpectedRollbackException.class})
	public User getUserByUserName(String userName) throws ResourceNotFoundException, UnexpectedRollbackException {
		User user;
		try {
			//Throws ResourceNotFoundException
			user = Optional.ofNullable(userRepository.findByUsername(userName)).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		} catch(ResourceNotFoundException rnfe) {
			log.error("Error while getting user = {} : {} ", userName, rnfe.toString());
			throw new ResourceNotFoundException("Error while getting user");
		} catch (Exception e) {
			log.error("Error while getting user = {} : {} ", userName, e.toString());
			throw new UnexpectedRollbackException("Error while getting user");
		}
		return user;
	}

	@Override
	@Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
	public User getUserById(Integer id) throws UnexpectedRollbackException {
		User user;
		try {
			//Throws ResourceNotFoundException | InvalidDataAccessApiUsageException
			user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		} catch (Exception e) {
			log.error("Error while getting user = {} : {} ", id, e.toString());
			throw new UnexpectedRollbackException("Error while getting user");
		}
		return user;
	}
	
	@Override
	@Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
	public User getUserByIdWithBlankPasswd(Integer userId) throws UnexpectedRollbackException {
		User user = getUserById(userId);
		user.setPassword("");
		return user;
	}

	@Override
	@Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
	public Page<User> getUsers(Pageable pageRequest) throws UnexpectedRollbackException {
		Page<User> pageUser;
		try {
			//Throws NullPointerException if pageRequest is null
			pageUser = userRepository.findAll(pageRequest).map(user -> {user.setPassword(""); return user;});
		} catch(Exception e) {
			log.error("Error while getting Users : {} ", e.toString());
			throw new UnexpectedRollbackException("Error while getting Users");
		}
		return pageUser;
	}

	@Override
	@Transactional(rollbackFor = {DataIntegrityViolationException.class, UnexpectedRollbackException.class})
	public User saveUser(User user) throws DataIntegrityViolationException, UnexpectedRollbackException {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		User userSaved;
		try {
			/*
			* No need to test blank or null fields for update because constraint validation on each field
			* @DynamicUpdate, Hibernate generates an UPDATE SQL statement that sets only columns that have changed
			* @Column(name = "username", unique = true) Throws DataIntegrityViolationException
			* Throws InvalidDataAccessApiUsageException | OptimisticLockingFailureException
			*/
			userSaved = userRepository.save(user);
		} catch(DataIntegrityViolationException dive) {
			log.error("Error while saving user = {} : {} ", user.toString(), dive.toString());
			throw new DataIntegrityViolationException("Username already exists");
		} catch(Exception e) {
			log.error("Error while saving user = {} : {} ", user.toString(), e.toString());
			throw new UnexpectedRollbackException("Error while saving user");
		}
		return userSaved;
	}

	@Override
	@Transactional(rollbackFor = {UnexpectedRollbackException.class})
	public void deleteUserById(Integer id) throws UnexpectedRollbackException {
		try {
			/* getUserById throws  UnexpectedRollbackException
			 * Throws InvalidDataAccessApiUsageException | OptimisticLockingFailureException */
			userRepository.delete(getUserById(id));
		} catch(Exception e) {
			log.error("Error while deleting user = {} : {} ", id, e.toString());
			throw new UnexpectedRollbackException("Error while deleting user");
		}
	}
}
