package com.poseidoninc.poseidon.service;

import com.poseidoninc.poseidon.domain.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * provides the user details for authentication and authorization (Username Password Authentication)
 *
 * @author olivier MOREL
 */
@Service
@Slf4j
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    /**
     * Get the username to try to get users' name, role and password from database
     * Return the user (UserDetails extends AuthenticatedPrincipal)
     * @param userName
     * @return user : principal
     * @throws UnexpectedRollbackException
     */
    @Override
    @Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
    public UserDetails loadUserByUsername(String userName) throws UnexpectedRollbackException {
        User user = userService.getUserByUserName(userName);
        List<GrantedAuthority> grantedAuthorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_"+user.getRole()));
        return new org.springframework.security.core.userdetails.User(user.getUsername() , user.getPassword(), grantedAuthorities);
    }
}