package com.poseidoninc.poseidon.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
@Table(name = "rating")
public class Rating {
	
	Integer id ;
	String moodysRating ;
	String sandPRating ;
	String fitchRating ;
	Integer orderNumber ;
}
