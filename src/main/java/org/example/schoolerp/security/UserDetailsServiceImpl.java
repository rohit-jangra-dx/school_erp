package org.example.schoolerp.security;

import java.util.stream.Stream;

import org.example.schoolerp.identity.entity.AuthAccount;
import org.example.schoolerp.identity.repos.AuthAccountRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService{
    
    private final AuthAccountRepository authAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // check if user exists or not
        AuthAccount account = authAccountRepository.findByUserUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("No user: " + username));
            
        // then gather his authorities
        var authorities = account.getUser().getRoles().stream()
            .flatMap(role -> Stream.concat(
                Stream.of(new SimpleGrantedAuthority(role.getName())), 
                role.getPermissions().stream().map(p -> new SimpleGrantedAuthority(p.getName()))
            ))
            .distinct()
            .toList();
        
            return org.springframework.security.core.userdetails.User
                .withUsername(account.getUser().getUsername())
                .password(account.getPasswordHash())
                .authorities(authorities)
                .accountLocked(account.isLocked())
                .build();
    }

}
