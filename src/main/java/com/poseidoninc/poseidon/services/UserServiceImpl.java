package com.poseidoninc.poseidon.services;

import java.util.Optional;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.User;
import com.poseidoninc.poseidon.exception.ResourceConflictException;
import com.poseidoninc.poseidon.exception.ResourceNotFoundException;
import com.poseidoninc.poseidon.repositories.UserRepository;

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
			//Throws ResourceNotFoundException | IllegalArgumentException
			user = Optional.ofNullable(userRepository.findByUsername(userName)).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		} catch(ResourceNotFoundException | IllegalArgumentException re) {
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
	@Transactional(readOnly = true, rollbackFor = {ResourceNotFoundException.class, IllegalArgumentException.class, UnexpectedRollbackException.class})
	public User getUserById(Integer id, WebRequest request) throws ResourceNotFoundException, IllegalArgumentException, UnexpectedRollbackException {
		User user = null;
		try {
			//Throws ResourceNotFoundException | IllegalArgumentException
			user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		} catch(IllegalArgumentException iae) {
			log.error("{} : {} ", requestService.requestToString(request), iae.toString());
			throw new IllegalArgumentException ("Id must not be null");
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
	public User getUserByIdWithBlankPasswd(Integer userId, WebRequest request) throws ResourceNotFoundException, IllegalArgumentException, UnexpectedRollbackException {
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
	@Transactional(rollbackFor = {ResourceNotFoundException.class, ResourceConflictException.class, UnexpectedRollbackException.class})
	public User saveUser(User user, WebRequest request) throws ResourceNotFoundException, ResourceConflictException, UnexpectedRollbackException {
		Integer id = user.getId(); //can be null;
		String username = user.getUsername();
		String oldUsername = null;
		try {
			oldUsername = getUserById(id, request).getUsername(); //throw ResourceNotFoundException, IllegalArgumentException, UnexpectedRollbackException
		} catch (IllegalArgumentException iae) {			
		} finally {
			if ((id == null || !username.equalsIgnoreCase(oldUsername)) && userRepository.existsByUsername(username)) {
				ResourceConflictException rce = new ResourceConflictException("UserName already exists");
				log.error("{} : user={} : {} ", requestService.requestToString(request), username, rce.toString());				
				throw rce;
			}
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		try {
			//No need to test blank or null fields for update because constraint validation on each field
			user = userRepository.save(user);
		}  catch(IllegalArgumentException | OptimisticLockingFailureException re) {
			log.error("{} : user={} : {} ", requestService.requestToString(request), user.getId(), re.toString());
			throw new UnexpectedRollbackException("Error while saving user");
		} catch(Exception e) {
			log.error("{} : user={} : {} ", requestService.requestToString(request), user.getId(), e.toString());
			throw new UnexpectedRollbackException("Error while saving user");
		}
		log.info("{} : user={} persisted", requestService.requestToString(request), user.getId());
		return user;
	}

	@Override
	@Transactional(rollbackFor = {IllegalArgumentException.class, ResourceNotFoundException.class, UnexpectedRollbackException.class})
	public void deleteUserById(Integer id, WebRequest request) throws IllegalArgumentException, ResourceNotFoundException, UnexpectedRollbackException {
		try {
			userRepository.delete(getUserById(id, request)); //getUserById throws ResourceNotFoundException, IllegalArgumentException, UnexpectedRollbackException
		} catch(IllegalArgumentException iae) {
			log.error("{} : user={} : {} ", requestService.requestToString(request), id, iae.toString());
			throw new UnexpectedRollbackException("Error while deleting user");
		} catch(ResourceNotFoundException  rnfe) {
			log.error("{} : user={} : {} ", requestService.requestToString(request), id, rnfe.toString());
			throw new ResourceNotFoundException(rnfe.getMessage());
		} catch(OptimisticLockingFailureException oe) {
			log.error("{} : user={} : {} ", requestService.requestToString(request), id, oe.toString());
			throw new UnexpectedRollbackException("Error while deleting user");
		} catch(Exception e) {
			log.error("{} : user={} : {} ", requestService.requestToString(request), id, e.toString());
			throw new UnexpectedRollbackException("Error while deleting user");
		}
		log.info("{} : user={} deleted", requestService.requestToString(request), id);
	}
}
