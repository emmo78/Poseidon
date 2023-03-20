package com.poseidoninc.poseidon.domain;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
@Table(name = "rating")
public class Rating {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id")
	Integer id;

	@Column(name = "moodysRating")
	@Size(max = 125, message = "MoodysRating must be maximum of 125 characters")
	String moodysRating;

	@Column(name = "sandPRating")
	@Size(max = 125, message = "SandPRating must be maximum of 125 characters")
	String sandPRating;

	@Column(name = "fitchRating")
	@Size(max = 125, message = "FitchRating must be maximum of 125 characters")
	String fitchRating;

	@Column(name = "orderNumber")
	@Min(value = 1, message = "OrderNumber must be positive")
	@Max(value = 127, message = "OrderNumber is a tinyint so max is 127")
	Integer orderNumber;
}
