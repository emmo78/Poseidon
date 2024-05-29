package com.poseidoninc.poseidon.configuration;

import com.poseidoninc.poseidon.service.CustomOAuth2UserService;
import com.poseidoninc.poseidon.service.CustomOidcUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Class for security configuration of the application.
 * filter chain, login/logout configurations, and OAuth2 login configurations.
 *
 * @author olivier morel
 */
@Configuration
@EnableWebSecurity
@AllArgsConstructor
@Slf4j
public class SecurityConfiguration  {

    private final PasswordEncoder passwordEncoder;

    private final UserDetailsService userDetailsService;

    private final CustomOAuth2UserService customOAuth2UserService;

    private final CustomOidcUserService customOidcUserService;

    /**
     * sets up the authentication manager bean (Username Password Authentication)
     * @see com.poseidoninc.poseidon.service.UserDetailsServiceImpl
     *
     * @return an instance of {@link AuthenticationManager}
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return new ProviderManager(provider);
    }

    /**
     * builds a SecurityFilterChain bean for the provided HttpSecurity.
     *
     * Also sets OAuth2UserService used for getting the user's information from the provider.
     * If the provider token is a OpenID Connect set also a OidcUserService
     * @see CustomOAuth2UserService
     * @see  CustomOidcUserService
     *
     * @param http the HttpSecurity object to build the SecurityFilterChain for
     * @return the built SecurityFilterChain
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/","/home", "/user/add", "/user/validate").permitAll()
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers( "/user/**").hasRole("ADMIN")
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
                                .oidcUserService(customOidcUserService))
                        .permitAll())
                .rememberMe(conf -> conf
                        .rememberMeParameter("remember")
                        .rememberMeCookieName("rememberlogin")
                        .tokenValiditySeconds(1200))
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/app-logout"))
                        .permitAll())
                .build();
    }
}