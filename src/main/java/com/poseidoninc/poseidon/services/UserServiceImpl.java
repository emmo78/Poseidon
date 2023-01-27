package com.poseidoninc.poseidon.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.User;
import com.poseidoninc.poseidon.exception.ResourceNotFoundException;
import com.poseidoninc.poseidon.repositories.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
	
	private final UserRepository userRepository;
	private final RequestService requestService;

	@Override
	@Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
	public User getUserByUserName(String userName, WebRequest request) throws UnexpectedRollbackException {
		User user = null;
		try {
			//Throws ResourceNotFoundException | IllegalArgumentException
			user = Optional.ofNullable(userRepository.findByUsername(userName)).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		} catch(ResourceNotFoundException | IllegalArgumentException re) {
			log.error("{} : {} ", requestService.requestToString(request), re.toString());
			throw new UnexpectedRollbackException("Error while getting user profile");
		} catch (Exception e) {
			log.error("{} : {} ", requestService.requestToString(request), e.toString());
			throw new UnexpectedRollbackException("Error while getting user profile");
		}
		log.info("{} : user={} gotten",  requestService.requestToString(request), user.getUsername());
		return user;
	}

	@Override
	@Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
	public User getUserById(Integer userId, WebRequest request) throws UnexpectedRollbackException {
		User user = null;
		try {
			//Throws ResourceNotFoundException | IllegalArgumentException
			user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		} catch(ResourceNotFoundException | IllegalArgumentException re) {
			log.error("{} : {} ", requestService.requestToString(request), re.toString());
			throw new UnexpectedRollbackException("Error while getting user profile");
		} catch (Exception e) {
			log.error("{} : {} ", requestService.requestToString(request), e.toString());
			throw new UnexpectedRollbackException("Error while getting user profile");
		}
		log.info("{} : user={} gotten",  requestService.requestToString(request), user.getId());
		return user;
	}

	@Override
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
	public User saveUser(Integer Id, User user, WebRequest request) throws UnexpectedRollbackException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteUser(Integer Id, WebRequest request) throws UnexpectedRollbackException {
		// TODO Auto-generated method stub

	}

}
