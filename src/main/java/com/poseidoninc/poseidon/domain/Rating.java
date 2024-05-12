package com.poseidoninc.poseidon.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "rating")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true, includeFieldNames=true)
public class Rating {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id")
	@ToString.Include
	Integer id;

	@Column(name = "moodysRating")
	@Size(max = 125, message = "MoodysRating must be maximum of 125 characters")
	@ToString.Include
	String moodysRating;

	@Column(name = "sandPRating")
	@Size(max = 125, message = "SandPRating must be maximum of 125 characters")
	@ToString.Include
	String sandPRating;

	@Column(name = "fitchRating")
	@Size(max = 125, message = "FitchRating must be maximum of 125 characters")
	@ToString.Include
	String fitchRating;

	@Column(name = "orderNumber")
	@Min(value = 1, message = "OrderNumber must be positive")
	@Max(value = 127, message = "OrderNumber is a tinyint so max is 127")
	@ToString.Include
	Integer orderNumber;
}
