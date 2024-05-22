package com.poseidoninc.poseidon.repository;

import com.poseidoninc.poseidon.domain.BidList;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * extends JpaRepository, providing CRUD operations for the BidList entity.
 * @see JpaRepository
 *
 * @author olivier morel
 */
public interface BidListRepository extends JpaRepository<BidList, Integer> {

}
