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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
@Table(name = "bidlist")
public class BidList {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "BidListId")
	Integer bidListId;

	@Column(name = "account")
	@NotBlank(message = "Account must be not null and not blank")
	@Size(max = 30, message = "Account must be maximum of 30 characters")
	String account;

	@Column(name = "type")
	@NotBlank(message = "Type must be not null and not blank")
	@Size(max = 30, message = "Type must be maximum of 30 characters")
	String type;

	@Column(name = "bidQuantity")
	@DecimalMin(value = "0.0", message = "Bidqantity must be a decimal number")
	@DecimalMax(value = "1.7976931348623157E308", inclusive = true, message = "Bidqantity must be a decimal number")
	Double bidQuantity;

	@Column(name = "askQuantity")
	@DecimalMin(value = "0.0", message = "AskQuantity must be a decimal number")
	@DecimalMax(value = "1.7976931348623157E308", inclusive = true, message = "AskQuantity must be a decimal number")
	Double askQuantity;

	@Column(name = "bid")
	@DecimalMin(value = "0.0", message = "Bid must be a decimal number")
	@DecimalMax(value = "1.7976931348623157E308", inclusive = true, message = "Bid must be a decimal number")
	Double bid;

	@Column(name = "ask")
	@DecimalMin(value = "0.0", message = "Ask must be a decimal number")
	@DecimalMax(value = "1.7976931348623157E308", inclusive = true, message = "Ask must be a decimal number")
	Double ask;

	@Column(name = "benchmark")
	@Size(max = 125, message = "Benchmark must be maximum of 125 characters")
	String benchmark;

	@Column(name = "bidListDate")
	@DateTimeFormat
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	LocalDateTime bidListDate;

	@Column(name = "commentary")
	@Size(max = 125, message = "Commentary must be maximum of 125 characters")
	String commentary;

	@Column(name = "security")
	@Size(max = 125, message = "Security must be maximum of 125 characters")
	String security;

	@Column(name = "status")
	@Size(max = 10, message = "Status must be maximum of 10 characters")
	String status;

	@Column(name = "trader")
	@Size(max = 125, message = "Trader must be maximum of 125 characters")
	String trader;

	@Column(name = "book")
	@Size(max = 125, message = "Book must be maximum of 125 characters")
	String book;

	@Column(name = "creationName")
	@Size(max = 125, message = "CreationName must be maximum of 125 characters")
	String creationName;

	@Column(name = "creationDate")
	@DateTimeFormat
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	LocalDateTime creationDate;

	@Column(name = "revisionName")
	@Size(max = 125, message = "RevisionName must be maximum of 125 characters")
	String revisionName;

	@Column(name = "revisionDate")
	@DateTimeFormat
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	LocalDateTime revisionDate;

	@Column(name = "dealName")
	@Size(max = 125, message = "dealName must be maximum of 125 characters")
	String dealName;

	@Column(name = "dealType")
	@Size(max = 125, message = "DealType must be maximum of 125 characters")
	String dealType;

	@Column(name = "sourceListId")
	@Size(max = 125, message = "SourceListId must be maximum of 125 characters")
	String sourceListId;

	@Column(name = "side")
	@Size(max = 125, message = "Side must be maximum of 125 characters")
	String side;
}
