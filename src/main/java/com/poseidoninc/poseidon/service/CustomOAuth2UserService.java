package com.poseidoninc.poseidon.service;

import com.poseidoninc.poseidon.domain.User;
import com.poseidoninc.poseidon.validator.ValidPasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
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
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final ParameterizedTypeReference<List<Map<String, String>>> PARAMETERIZED_RESPONSE_TYPE = new ParameterizedTypeReference<>() {};

    private final UserService userService;

    private final ValidPasswordGenerator validPasswordGenerator;

    @Override
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
        Integer id = null;
        try {
            id=userService.getUserByUserName(email).getId();
            log.info("user id = {} exists", id);
        } catch (ResourceNotFoundException rnfe) {
            log.info("user not exist");
        }
        User user = User.builder()
                .id(id)
                .username(email)
                .password(validPasswordGenerator.generatePassword())//ToDo
                .fullname(Objects.requireNonNull(attributes.get("name").toString()))
                .role("USER")
                .build();
        user = userService.saveUser(user);
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole()));
        return new DefaultOAuth2User(authorities, attributes, "name");
    }
}

