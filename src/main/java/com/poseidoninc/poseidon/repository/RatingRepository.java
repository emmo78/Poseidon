package com.poseidoninc.poseidon.repository;

import com.poseidoninc.poseidon.domain.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Integer> {

}
