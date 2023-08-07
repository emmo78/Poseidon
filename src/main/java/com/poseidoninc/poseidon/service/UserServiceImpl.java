package com.poseidoninc.poseidon.service;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.User;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import com.poseidoninc.poseidon.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
	
	private final UserRepository userRepository;
	private final RequestService requestService;
	private final PasswordEncoder passwordEncoder;
	
	@Override
	@Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
	public User getUserByUserName(String userName, WebRequest request) throws UnexpectedRollbackException {
		User user = null;
		try {
			//Throws ResourceNotFoundException | InvalidDataAccessApiUsageException
			user = Optional.ofNullable(userRepository.findByUsername(userName)).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		} catch(ResourceNotFoundException | InvalidDataAccessApiUsageException re) {
			log.error("{} : user={} : {} ", requestService.requestToString(request), userName, re.toString());
			throw new UnexpectedRollbackException("Error while getting user profile");
		} catch (Exception e) {
			log.error("{} : user={} : {} ", requestService.requestToString(request), userName, e.toString());
			throw new UnexpectedRollbackException("Error while getting user profile");
		}
		log.info("{} : user={} gotten",  requestService.requestToString(request), user.getUsername());
		return user;
	}

	@Override
	@Transactional(readOnly = true, rollbackFor = {ResourceNotFoundException.class, InvalidDataAccessApiUsageException.class, UnexpectedRollbackException.class})
	public User getUserById(Integer id, WebRequest request) throws ResourceNotFoundException, InvalidDataAccessApiUsageException, UnexpectedRollbackException {
		User user = null;
		try {
			//Throws ResourceNotFoundException | InvalidDataAccessApiUsageException
			user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		} catch(InvalidDataAccessApiUsageException iae) {
			log.error("{} : {} ", requestService.requestToString(request), iae.toString());
			throw new InvalidDataAccessApiUsageException ("Id must not be null");
		} catch(ResourceNotFoundException  rnfe) {
			log.error("{} : user={} : {} ", requestService.requestToString(request), id, rnfe.toString());
			throw new ResourceNotFoundException(rnfe.getMessage());
		} catch (Exception e) {
			log.error("{} : user={} : {} ", requestService.requestToString(request), id, e.toString());
			throw new UnexpectedRollbackException("Error while getting user profile");
		}
		log.info("{} : user={} gotten",  requestService.requestToString(request), user.getId());
		return user;
	}
	
	@Override
	public User getUserByIdWithBlankPasswd(Integer userId, WebRequest request) throws ResourceNotFoundException, InvalidDataAccessApiUsageException, UnexpectedRollbackException {
		User user = getUserById(userId, request);
		user.setPassword("");
		return user;
	}

	@Override
	@Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
	public Page<User> getUsers(Pageable pageRequest, WebRequest request) throws UnexpectedRollbackException {
		Page<User> pageUsers = null;
		try {
			//throws NullPointerException if pageRequest is null
			pageUsers = userRepository.findAll(pageRequest);
		} catch(NullPointerException npe) {
			log.error("{} : {} ", requestService.requestToString(request), npe.toString());
			throw new UnexpectedRollbackException("Error while getting Users");
		} catch(Exception e) {
			log.error("{} : {} ", requestService.requestToString(request), e.toString());
			throw new UnexpectedRollbackException("Error while getting Users");
		}
		log.info("{} : users page number : {} of {}",
			requestService.requestToString(request),
			pageUsers.getNumber()+1,
			pageUsers.getTotalPages());
		return pageUsers;
	}

	@Override
	@Transactional(rollbackFor = {DataIntegrityViolationException.class, UnexpectedRollbackException.class})
	public User saveUser(User user, WebRequest request) throws DataIntegrityViolationException, UnexpectedRollbackException {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		try {
			//No need to test blank or null fields for update because constraint validation on each field
			user = userRepository.save(user);
		} catch(InvalidDataAccessApiUsageException | OptimisticLockingFailureException re) {
			log.error("{} : user={} : {} ", requestService.requestToString(request), user.getId(), re.toString());
			throw new UnexpectedRollbackException("Error while saving user");
		} catch(DataIntegrityViolationException dive) {
			log.error("{} : user={} : {} ", requestService.requestToString(request), user.getId(), dive.toString());
			throw new DataIntegrityViolationException("Username already exist, try another one");
		} catch(Exception e) {
			log.error("{} : user={} : {} ", requestService.requestToString(request), user.getId(), e.toString());
			throw new UnexpectedRollbackException("Error while saving user");
		}
		log.info("{} : user={} persisted", requestService.requestToString(request), user.getId());
		return user;
	}

	@Override
	@Transactional(rollbackFor = {ResourceNotFoundException.class, UnexpectedRollbackException.class})
	public void deleteUserById(Integer id, WebRequest request) throws ResourceNotFoundException, UnexpectedRollbackException {
		try {
			userRepository.delete(getUserById(id, request)); //getUserById throws ResourceNotFoundException, InvalidDataAccessApiUsageException, UnexpectedRollbackException
		} catch(InvalidDataAccessApiUsageException | OptimisticLockingFailureException re) {
			log.error("{} : user={} : {} ", requestService.requestToString(request), id, re.toString());
			throw new UnexpectedRollbackException("Error while deleting user");
		} catch(ResourceNotFoundException  rnfe) {
			log.error("{} : user={} : {} ", requestService.requestToString(request), id, rnfe.toString());
			throw new ResourceNotFoundException(rnfe.getMessage());
		} catch(Exception e) {
			log.error("{} : user={} : {} ", requestService.requestToString(request), id, e.toString());
			throw new UnexpectedRollbackException("Error while deleting user");
		}
		log.info("{} : user={} deleted", requestService.requestToString(request), id);
	}
}
