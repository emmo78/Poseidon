package com.poseidoninc.poseidon.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
	Integer BidListId ;
	
	@Column(name = "account")
	@NotBlank(message ="Account must be not null and not blank")
	@Size(max = 30, message = "Account must be maximum of 30 characters")
	String account ;

	@Column(name = "type")
	@NotBlank(message ="Type must be not null and not blank")
	@Size(max = 30, message = "Type must be maximum of 30 characters")
	String type ;

	@Column(name = "bidQuantity")
	@DecimalMin(value = "-1.7976931348623157E308", inclusive = true, message = "Bidqantity must be a decimal number")
	@DecimalMax(value = "1.7976931348623157E308", inclusive = true, message = "Bidqantity must be a decimal number")
	Double bidQuantity ;

	@Column(name = "askQuantity")
	@DecimalMin(value = "-1.7976931348623157E308", inclusive = true, message = "AskQuantity must be a decimal number")
	@DecimalMax(value = "1.7976931348623157E308", inclusive = true, message = "AskQuantity must be a decimal number")
	Double askQuantity ;

	@Column(name = "bid")
	@DecimalMin(value = "-1.7976931348623157E308", inclusive = true, message = "Bid must be a decimal number")
	@DecimalMax(value = "1.7976931348623157E308", inclusive = true, message = "Bid must be a decimal number")
	Double bid ;

	@Column(name = "ask")
	@DecimalMin(value = "-1.7976931348623157E308", inclusive = true, message = "Ask must be a decimal number")
	@DecimalMax(value = "1.7976931348623157E308", inclusive = true, message = "Ask must be a decimal number")
	Double ask ;

	@Column(name = "benchmark")
	String benchmark ;

	@Column(name = "bidListDate")
	LocalDateTime bidListDate ;

	@Column(name = "commentary")
	String commentary ;

	@Column(name = "security")
	String security ;

	@Column(name = "status")
	String status ;

	@Column(name = "trader")
	String trader ;

	@Column(name = "book")
	String book ;

	@Column(name = "creationName")
	String creationName ;

	@Column(name = "creationDate")
	LocalDateTime creationDate ;

	@Column(name = "revisionName")
	String revisionName ;
	
	@Column(name = "revisionDate")
	LocalDateTime revisionDate ;
	
	@Column(name = "dealName")
	String dealName ;
	
	@Column(name = "dealType")
	String dealType ;
	
	@Column(name = "sourceListId")
	String sourceListId ;
	
	@Column(name = "side")
	String side ;
}
