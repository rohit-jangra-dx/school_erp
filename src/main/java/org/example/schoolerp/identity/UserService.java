package org.example.schoolerp.identity;

import org.example.schoolerp.identity.entity.Role;
import org.example.schoolerp.identity.entity.User;
import org.example.schoolerp.identity.repos.UserRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final AuthService authService;

    public String createUser(String username, String rawPassword) {

        var user = userRepository.save(new User(username));
        user.addRole(new Role("USER"));
        return authService.createAuthAccount(user, rawPassword);
    }

    public void DeleteUser(String username) {
        var user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("No user found: " + username));
        userRepository.delete(user);
    }

    
    
}
