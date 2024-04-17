package com.poseidoninc.poseidon.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final UserService userService;

    private final OidcUserService oidcUserService = new OidcUserService();

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {

        OidcUser oidcUser = (OidcUser) oidcUserService.loadUser(userRequest);
        Map<String, Object> attributes = new HashMap<>(oidcUser.getAttributes());
        attributes = oidcUser.getAttributes();
        attributes.forEach((name, value) -> log.info("name : {} - value : {}", name, value!=null?value.toString():"null"));

        /*String email = (String) attributes.get("email");

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found: " + email));

        attributes.put("custom:role", user.getRole());

        return new DefaultOidcUser(Collections.singleton(new SimpleGrantedAuthority(user.getRole())), oidcUser.getIdToken(), oidcUser.getUserInfo(), "custom:role");
         */
        return oidcUser;
    }
}