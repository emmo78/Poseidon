package com.poseidoninc.poseidon.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.User;
import com.poseidoninc.poseidon.exception.ResourceConflictException;
import com.poseidoninc.poseidon.exception.ResourceNotFoundException;

public interface UserService {
	User getUserByUserName(String userName, WebRequest request) throws UnexpectedRollbackException;
	User getUserById(Integer id, WebRequest request) throws ResourceNotFoundException, IllegalArgumentException, UnexpectedRollbackException;
	User getUserByIdWithBlankPasswd(Integer userId, WebRequest request) throws ResourceNotFoundException, IllegalArgumentException, UnexpectedRollbackException;
	Page<User> getUsers(Pageable pageRequest, WebRequest request) throws UnexpectedRollbackException;
	User saveUser(User user, WebRequest request) throws ResourceConflictException, ResourceNotFoundException, UnexpectedRollbackException;
	void deleteUserById(Integer id, WebRequest request) throws UnexpectedRollbackException;	
}
