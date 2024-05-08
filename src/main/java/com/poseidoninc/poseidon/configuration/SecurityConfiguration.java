package com.poseidoninc.poseidon.configuration;

import com.poseidoninc.poseidon.service.CustomOAuth2UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@Slf4j
public class SecurityConfiguration  {

    private final PasswordEncoder passwordEncoder;

    private final UserDetailsService userDetailsService;

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return new ProviderManager(provider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/","/home", "/user/add", "/user/validate").permitAll()
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/admin/home", "/user/**").hasRole("ADMIN")
                        .requestMatchers( "/curvePoint/**", "/bidList/**", "/rating/**", "/ruleName/**", "/trade/**").hasAnyRole("ADMIN", "USER")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                        .permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                                .oidcUserService(this.oidcUserService()))
                        .permitAll())
                .rememberMe(conf -> conf
                        .rememberMeParameter("remember")
                        .rememberMeCookieName("rememberlogin")
                        .tokenValiditySeconds(1200))
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/app-logout"))
                        .permitAll());
        return http.build();
    }

    private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        final OidcUserService delegate = new OidcUserService();

        return (userRequest) -> {
            // Delegate to the default implementation for loading a user
            OidcUser oidcUser = delegate.loadUser(userRequest);

            OAuth2AccessToken accessToken = userRequest.getAccessToken();
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

        // 1) Fetch the authority information from the protected resource using accessToken
        // Let's assume the protected resource returns a list of authorities for the user
            List<String> authorities = fetchUserAuthoritiesFromProtectedResource(accessToken);

        // 2) Map the authority information to one or more GrantedAuthority's and add it to mappedAuthorities
            authorities.stream().map(SimpleGrantedAuthority::new).forEach(mappedAuthorities::add);

            // 3) Create a copy of oidcUser but use the mappedAuthorities instead
            oidcUser = new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());

            return oidcUser;
        };
    }


    private List<String> fetchUserAuthoritiesFromProtectedResource(OAuth2AccessToken accessToken) {
        // As this method needs to be implemented according to the specifics of your protected resource,
        // Placeholder implementation is shown here.
        // Typically, it will involve making a network call to the resource with the access token and parsing the response.

        return Arrays.asList("ROLE_USER", "ROLE_ADMIN");  // placeholder return. Implement this method as per your requirements
}
}