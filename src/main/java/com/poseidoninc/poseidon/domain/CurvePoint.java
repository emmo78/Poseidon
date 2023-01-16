package com.poseidoninc.poseidon.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Entity
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
@Table(name = "curvepoint")
public class CurvePoint {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id")
	Integer id;

	@Column(name = "CurveId")
	@Min(value = 0, message = "CurveId must be positive")
	@Max(value = 127, message = "CurveId is a tinyint so max is 127")
	Integer curveId;

	@Column(name = "asOfDate")
	@DateTimeFormat
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	LocalDateTime asOfDate;

	@Column(name = "term")
	@DecimalMin(value = "0.0", message = "Term must be a decimal number")
	@DecimalMax(value = "1.7976931348623157E308", inclusive = true, message = "Term must be a decimal number")
	Double term;

	@Column(name = "value")
	@DecimalMin(value = "0.0", message = "Value must be a decimal number")
	@DecimalMax(value = "1.7976931348623157E308", inclusive = true, message = "Value must be a decimal number")
	Double value;

	@Column(name = "creationDate")
	@DateTimeFormat
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	LocalDateTime creationDate;
}
