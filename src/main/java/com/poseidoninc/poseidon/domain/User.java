package com.poseidoninc.poseidon.domain;

import com.poseidoninc.poseidon.annotation.ValidPassword;
import com.poseidoninc.poseidon.annotation.ValidPasswordGroup;
import jakarta.persistence.*;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * User entity
 * GroupSequence annotation is useful to control
 * the order in which constraints are evaluated,
 * to test NotNull annotation (User.class) before
 * ValidPassword annotation (ValidPasswordGroup.class)
 * to avoid NullPointerException
 *
 * @see ValidPassword
 * @see ValidPasswordGroup
 *
 * @author olivier morel
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "users")
@GroupSequence({User.class, ValidPasswordGroup.class})
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true, includeFieldNames=true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "Id")
    @ToString.Include
    private Integer id;

    /**
     * The username variable has @Column annotation with the unique parameter set to true, indicating that the username must be unique in the table.
     * Throw a DataIntegrityViolationException.
     */
    @Column(name = "username", unique = true)
    @NotBlank(message = "Username is mandatory")
	@Size(max = 125, message = "Username must be maximum of 125 characters")
    @ToString.Include
    private String username;
	
    @Column(name = "password")
    @NotNull(message = "Password is mandatory")
    @ValidPassword(groups = ValidPasswordGroup.class)
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
