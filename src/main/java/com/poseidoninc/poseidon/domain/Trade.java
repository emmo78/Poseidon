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
 * Trade Entity
 *
 * @author olivier morel
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "trade")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true, includeFieldNames=true)
public class Trade {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "TradeId")
	@ToString.Include
	Integer tradeId;

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
	
	@Column(name = "buyQuantity")
	@DecimalMin(value = "0.0", message = "BuyQuantity must be a decimal number")
	@DecimalMax(value = "1.7976931348623157E308", inclusive = true, message = "BuyQuantity must be a decimal number")
	@ToString.Include
	Double buyQuantity;
	
	@Column(name = "sellQuantity")
	@DecimalMin(value = "0.0", message = "SellQuantity must be a decimal number")
	@DecimalMax(value = "1.7976931348623157E308", inclusive = true, message = "SellQuantity must be a decimal number")
	Double sellQuantity;
	
	@Column(name = "buyPrice")
	@DecimalMin(value = "0.0", message = "BuyPrice must be a decimal number")
	@DecimalMax(value = "1.7976931348623157E308", inclusive = true, message = "BuyPrice must be a decimal number")
	Double buyPrice;

	@Column(name = "sellPrice")
	@DecimalMin(value = "0.0", message = "SellPrice must be a decimal number")
	@DecimalMax(value = "1.7976931348623157E308", inclusive = true, message = "SellPrice must be a decimal number")
	Double sellPrice;

	@Column(name = "tradeDate")
	@DateTimeFormat
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	LocalDateTime tradeDate;

	@Column(name = "security")
	@Size(max = 125, message = "Security must be maximum of 125 characters")
	String security;

	@Column(name = "status")
	@Size(max = 10, message = "Status must be maximum of 10 characters")
	String status;

	@Column(name = "trader")
	@Size(max = 125, message = "Trader must be maximum of 125 characters")
	String trader;

	@Column(name = "benchmark")
	@Size(max = 125, message = "Benchmark must be maximum of 125 characters")
	String benchmark;

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
	@Size(max = 125, message = "DealName must be maximum of 125 characters")
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
