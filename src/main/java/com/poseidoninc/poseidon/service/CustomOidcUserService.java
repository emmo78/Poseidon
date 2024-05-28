package com.poseidoninc.poseidon.service;

import com.poseidoninc.poseidon.domain.User;
import com.poseidoninc.poseidon.validator.ValidPasswordGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Customize the loading of the OidcUser.
 *
 * @author olivier morel
 */
@Service
@AllArgsConstructor
@Slf4j
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final UserService userService;

    private final OidcUserService oidcUserService = new OidcUserService();

    private final ValidPasswordGenerator validPasswordGenerator;

    /**
     * Get the Oidc token and use it to get users' email and name attributes.
     * Then persist as new user or update it in database.
     * Return the user (OidcUser extends OAuth2User, AuthenticatedPrincipal)
     *
     * @param userRequest
     * @return OAuth2User : principal
     * @throws OAuth2AuthenticationException
     */
    @Override
    //@Transactional(rollbackFor = {UnexpectedRollbackException.class, DataIntegrityViolationException.class})
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = (OidcUser) oidcUserService.loadUser(userRequest);
        Map<String, Object> attributes = oidcUser.getClaims();
        String email = (String) attributes.get("email");
        User user = null;
        try {
            user = userService.getUserByUserName(email);
            log.info("user = {} exists", user.toString());
        } catch (ResourceNotFoundException rnfe) {
            log.info("user does not exist");
        }
        //update user if exists or save new one
        //for update : @DynamicUpdate, Hibernate generates an UPDATE SQL statement that sets only columns that have changed
        user = User.builder()
                .id(user!=null?user.getId():null)
                .username(email)
                .password(validPasswordGenerator.generatePassword())
                .fullname(Objects.requireNonNull(attributes.get("name").toString()))
                .role("USER")
                .build();
        user = userService.saveUser(user);
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole()));
        return new DefaultOidcUser(authorities, oidcUser.getIdToken(), "name");
    }
}