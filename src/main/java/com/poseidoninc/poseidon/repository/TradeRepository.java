package com.poseidoninc.poseidon.repository;

import com.poseidoninc.poseidon.domain.Trade;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * extends JpaRepository, providing CRUD operations for the Trade entity.
 * @see JpaRepository
 *
 * @author olivier morel
 */
public interface TradeRepository extends JpaRepository<Trade, Integer> {
}
