package com.poseidoninc.poseidon.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "curvepoint")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true, includeFieldNames=true)
public class CurvePoint {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id")
	@ToString.Include
	Integer id;

	/**
	 * The curveId variable has @Column annotation with the unique parameter set to true, indicating that the username must be unique in the table.
	 * Throw a DataIntegrityViolationException.
	 */
	@Column(name = "CurveId", unique = true)
	@NotNull(message = "must not be null")
	@Min(value = 1, message = "CurveId min is 1")
	@Max(value = 127, message = "CurveId is a tinyint so max is 127")
	@ToString.Include
	Integer curveId;

	@Column(name = "asOfDate")
	@DateTimeFormat
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	LocalDateTime asOfDate;

	@Column(name = "term")
	@DecimalMin(value = "0.0", message = "Term must be a decimal number")
	@DecimalMax(value = "1.7976931348623157E308", inclusive = true, message = "Term must be a decimal number")
	@ToString.Include
	Double term;

	@Column(name = "`value`")
	@DecimalMin(value = "0.0", message = "Value must be a decimal number")
	@DecimalMax(value = "1.7976931348623157E308", inclusive = true, message = "Value must be a decimal number")
	@ToString.Include
	Double value;

	@Column(name = "creationDate")
	@DateTimeFormat
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	LocalDateTime creationDate;
}
