package com.poseidoninc.poseidon.repository;

import com.poseidoninc.poseidon.domain.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * extends JpaRepository, providing CRUD operations for the Rating entity.
 * @see JpaRepository
 *
 * @author olivier morel
 */
public interface RatingRepository extends JpaRepository<Rating, Integer> {

}
