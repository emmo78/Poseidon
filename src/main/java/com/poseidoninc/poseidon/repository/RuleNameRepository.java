package com.poseidoninc.poseidon.repository;

import com.poseidoninc.poseidon.domain.RuleName;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * extends JpaRepository, providing CRUD operations for the RuleName entity.
 * @see JpaRepository
 *
 * @author olivier morel
 */
public interface RuleNameRepository extends JpaRepository<RuleName, Integer> {
}
