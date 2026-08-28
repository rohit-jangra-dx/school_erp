package org.example.schoolerp.identity;

import org.example.schoolerp.identity.entity.Role;
import org.example.schoolerp.identity.entity.User;
import org.example.schoolerp.identity.repos.UserRepository;
import org.example.schoolerp.organization.Organization;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final AuthService authService;

    public String createUser(String username) {

        var user = userRepository.save(new User("dash"));
        user.addRole(new Role("USER"));
        return authService.createAuthAccount(user, "rawPassword");
    }

    // public void DeleteUser(String username, Organization organization) {
    //     var user = userRepository.findB
    // }

    
}
