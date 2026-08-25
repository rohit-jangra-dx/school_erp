package org.example.schoolerp.identity;

import org.example.schoolerp.identity.entity.AuthAccount;
import org.example.schoolerp.identity.repos.AuthAccountRepository;
import org.example.schoolerp.security.auth.JwtService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
   private final AuthAccountRepository authAccountRepository;
   private final JwtService jwtService;
   
   @Transactional(readOnly = true)
   public String generateTokenFromUsername(String username) {
        AuthAccount authAccount = authAccountRepository.findByUserUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        return jwtService.generateToken(authAccount);
   }
}
