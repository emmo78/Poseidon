package com.poseidoninc.poseidon.service;

import com.poseidoninc.poseidon.domain.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.context.request.WebRequest;

public interface UserService {
	User getUserByUserName(String userName, WebRequest request) throws UnexpectedRollbackException;
	User getUserById(Integer id, WebRequest request) throws UnexpectedRollbackException;
	User getUserByIdWithBlankPasswd(Integer userId, WebRequest request) throws UnexpectedRollbackException;
	Page<User> getUsers(Pageable pageRequest, WebRequest request) throws UnexpectedRollbackException;
	User saveUser(User user, WebRequest request) throws DataIntegrityViolationException, UnexpectedRollbackException;
	void deleteUserById(Integer id, WebRequest request) throws UnexpectedRollbackException;
}
