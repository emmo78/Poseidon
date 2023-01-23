package com.poseidoninc.poseidon.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.WebRequest;

import com.poseidoninc.poseidon.domain.User;

public interface UserService {
	User getUserByUserName(String userName, WebRequest request) throws UnexpectedRollbackException;
	User getUserById(Integer userId, WebRequest request) throws UnexpectedRollbackException;
	Page<User> getUsers(Pageable pageRequest, WebRequest request) throws UnexpectedRollbackException;
	User saveUser(Integer Id, User user, WebRequest request) throws UnexpectedRollbackException;
	void deleteUser(Integer Id, WebRequest request) throws UnexpectedRollbackException;	
}
