package com.poseidoninc.poseidon.service;

import com.poseidoninc.poseidon.domain.User;
import com.poseidoninc.poseidon.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Override
    @Transactional(readOnly = true, rollbackFor = UnexpectedRollbackException.class)
    public UserDetails loadUserByUsername(String userName) throws UnexpectedRollbackException {
        User user = userService.getUserByUserName(userName);
        List<GrantedAuthority> grantedAuthorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_"+user.getRole()));
        return new org.springframework.security.core.userdetails.User(user.getUsername() , user.getPassword(), grantedAuthorities);
    }
}