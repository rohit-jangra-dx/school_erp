package org.example.schoolerp.identity;

import org.example.schoolerp.identity.entity.AuthAccount;
import org.example.schoolerp.identity.entity.User;
import org.example.schoolerp.identity.repos.AuthAccountRepository;
import org.example.schoolerp.security.auth.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthAccountRepository authAccountRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public String generateTokenFromUsername(String username) {
        AuthAccount authAccount = authAccountRepository.findByUserUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        return jwtService.generateToken(authAccount);
    }

    @Transactional(readOnly = true)
    public String createAuthAccount(User user, String rawPassword) {
        var authAccount = new AuthAccount(user, passwordEncoder.encode(rawPassword));
        authAccount = authAccountRepository.save(authAccount);

        return jwtService.generateToken(authAccount);
    }
}
