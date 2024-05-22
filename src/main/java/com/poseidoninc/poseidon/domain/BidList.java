package com.poseidoninc.poseidon.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * BidList Entity
 *
 * @author olivier morel
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "bidlist")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true, includeFieldNames=true)
public class BidList {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "BidListId")
	@ToString.Include
	Integer bidListId;

	@Column(name = "account")
	@NotBlank(message = "Account is mandatory")
	@Size(max = 30, message = "Account must be maximum of 30 characters")
	@ToString.Include
	String account;

	@Column(name = "type")
	@NotBlank(message = "Type is mandatory")
	@Size(max = 30, message = "Type must be maximum of 30 characters")
	@ToString.Include
	String type;

	@Column(name = "bidQuantity")
	@DecimalMin(value = "0.0", message = "Bidqantity must be a positive decimal number")
	@DecimalMax(value = "1.7976931348623157E308", inclusive = true, message = "Bidqantity must be a positive decimal number")
	@ToString.Include
	Double bidQuantity;

	@Column(name = "askQuantity")
	@DecimalMin(value = "0.0", message = "AskQuantity must be a positive decimal number")
	@DecimalMax(value = "1.7976931348623157E308", inclusive = true, message = "AskQuantity must be a positive decimal number")
	Double askQuantity;

	@Column(name = "bid")
	@DecimalMin(value = "0.0", message = "Bid must be a positive decimal number")
	@DecimalMax(value = "1.7976931348623157E308", inclusive = true, message = "Bid must be a positive decimal number")
	Double bid;

	@Column(name = "ask")
	@DecimalMin(value = "0.0", message = "Ask must be a positive decimal number")
	@DecimalMax(value = "1.7976931348623157E308", inclusive = true, message = "Ask must be a positive decimal number")
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
