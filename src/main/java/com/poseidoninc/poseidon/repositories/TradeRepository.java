package com.poseidoninc.poseidon.repositories;

import com.poseidoninc.poseidon.domain.Trade;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TradeRepository extends JpaRepository<Trade, Integer> {
}
