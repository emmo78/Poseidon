package com.poseidoninc.poseidon.repository;

import com.poseidoninc.poseidon.domain.CurvePoint;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * extends JpaRepository, providing CRUD operations for the CurvePoint entity.
 * @see JpaRepository
 *
 * @author olivier morel
 */

public interface CurvePointRepository extends JpaRepository<CurvePoint, Integer> {
}
