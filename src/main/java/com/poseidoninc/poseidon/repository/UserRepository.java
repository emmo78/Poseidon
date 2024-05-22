package com.poseidoninc.poseidon.repository;

import com.poseidoninc.poseidon.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


/**
 * extends JpaRepository, providing CRUD operations for the User entity.
 * @see JpaRepository
 *
 * @author olivier morel
 */
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

	/**
	 * JPA Named Queries to get a user by his user name
	 * @param userName
	 * @return
	 */
	User findByUsername(String userName);

}
