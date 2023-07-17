package com.poseidoninc.poseidon.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.poseidoninc.poseidon.domain.User;


public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

	User findByUsername(String userName);
	boolean existsByUsernameIgnoreCase(String userName);
}
