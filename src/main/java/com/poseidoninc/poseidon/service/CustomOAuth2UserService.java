package com.poseidoninc.poseidon.service;

import com.poseidoninc.poseidon.domain.User;
import com.poseidoninc.poseidon.validator.ValidPasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Customize the loading of the OAuth2User
 *
 * @author olivier morel
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final ParameterizedTypeReference<List<Map<String, String>>> PARAMETERIZED_RESPONSE_TYPE = new ParameterizedTypeReference<>() {};

    private final UserService userService;

    private final ValidPasswordGenerator validPasswordGenerator;

    @Override
    //@Transactional(rollbackFor = {UnexpectedRollbackException.class, DataIntegrityViolationException.class})
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        //Get from GitHub the user access token see https://docs.github.com/fr/apps/oauth-apps/building-oauth-apps/scopes-for-oauth-apps#available-scopes
        //Read OAuth2.properties for Github's scopes
        String accessTokenValue = userRequest.getAccessToken().getTokenValue();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + accessTokenValue);
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<List<Map<String, String>>> response = restTemplate.exchange(
                "https://api.github.com/user/emails",
                HttpMethod.GET,
                entity,
                PARAMETERIZED_RESPONSE_TYPE);
        String email = Objects.requireNonNull(response.getBody()).stream().filter(map -> map.get("primary").equals("true")).map(map -> map.get("email")).toList().get(0);
        //get user if exists
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
        return new DefaultOAuth2User(authorities, attributes, "name");
    }
}

