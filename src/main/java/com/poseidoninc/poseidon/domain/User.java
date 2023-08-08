package com.poseidoninc.poseidon.domain;

import com.poseidoninc.poseidon.annotation.ValidPassword;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "Id")
    private Integer id;
    
	@Column(name = "username", unique = true) //@Column(unique = true)
    @NotBlank(message = "Username is mandatory")
	@Size(max = 125, message = "Username must be maximum of 125 characters")
    private String username;
	
    @Column(name = "password")
    @NotNull(message = "Password is mandatory")
    @ValidPassword
    private String password;

	@Column(name = "fullname")
    @NotBlank(message = "Fullname is mandatory")
	@Size(max = 125, message = "Fullname must be maximum of 125 characters")
    private String fullname;
 
	@Column(name = "role")
    @NotBlank(message = "Role is mandatory")
	@Size(max = 125, message = "Role must be maximum of 125 characters")
    private String role;
}
