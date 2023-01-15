package com.poseidoninc.poseidon.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
@Table(name = "rulename")
public class RuleName {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id")
	Integer id ;
	
	@Column(name = "name")
	@Size(max = 125, message = "Name must be maximum of 125 characters")
	String name ;

	@Column(name = "description")
	@Size(max = 125, message = "Description must be maximum of 125 characters")
	String description ;

	@Column(name = "json")
	@Size(max = 125, message = "Json must be maximum of 125 characters")
	String json ;

	@Column(name = "template")
	@Size(max = 125, message = "Template must be maximum of 125 characters")
	String template ;

	@Column(name = "sqlStr")
	@Size(max = 125, message = "SqlStr must be maximum of 125 characters")
	String sqlStr ;

	@Column(name = "sqlPart")
	@Size(max = 125, message = "sqlPart must be maximum of 125 characters")
	String sqlPart ;
}
