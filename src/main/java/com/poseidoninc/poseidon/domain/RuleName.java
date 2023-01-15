package com.poseidoninc.poseidon.domain;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

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
	Integer id;

	@Column(name = "name")
	@Size(max = 125, message = "Name must be maximum of 125 characters")
	String name;

	@Column(name = "description")
	@Size(max = 125, message = "Description must be maximum of 125 characters")
	String description;

	@Column(name = "json")
	@Size(max = 125, message = "Json must be maximum of 125 characters")
	String json;

	@Column(name = "template")
	@Size(max = 125, message = "Template must be maximum of 125 characters")
	String template;

	@Column(name = "sqlStr")
	@Size(max = 125, message = "SqlStr must be maximum of 125 characters")
	String sqlStr;

	@Column(name = "sqlPart")
	@Size(max = 125, message = "sqlPart must be maximum of 125 characters")
	String sqlPart;
}
