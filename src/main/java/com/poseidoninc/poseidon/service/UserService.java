package com.poseidoninc.poseidon.service;

import com.poseidoninc.poseidon.domain.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.transaction.UnexpectedRollbackException;

/**
 * Service for User
 *
 * @author olivier morel
 */
public interface UserService {
	/**
	 * Gets user by his username, used by authentications
	 *
	 * @param userName
	 * @return
	 * @throws ResourceNotFoundException
	 * @throws UnexpectedRollbackException
	 */
	User getUserByUserName(String userName) throws ResourceNotFoundException, UnexpectedRollbackException;
	User getUserById(Integer id) throws UnexpectedRollbackException;
	User getUserByIdWithBlankPasswd(Integer userId) throws UnexpectedRollbackException;
	Page<User> getUsers(Pageable pageRequest) throws UnexpectedRollbackException;

	/**
	 * Throws DataIntegrityViolationException if unique violation on user name
	 *
	 * @param user
	 * @return
	 * @throws DataIntegrityViolationException
	 * @throws UnexpectedRollbackException
	 */
	User saveUser(User user) throws DataIntegrityViolationException, UnexpectedRollbackException;
	void deleteUserById(Integer id) throws UnexpectedRollbackException;
}
