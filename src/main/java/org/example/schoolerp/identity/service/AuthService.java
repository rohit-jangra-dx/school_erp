package org.example.schoolerp.identity.service;

import lombok.RequiredArgsConstructor;
import org.example.schoolerp.identity.entity.AuthAccount;
import org.example.schoolerp.identity.repo.AuthAccountRepository;
import org.example.schoolerp.security.auth.JwtService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final AuthAccountRepository authAccountRepository;
  private final JwtService jwtService;

  @Transactional(readOnly = true)
  public String generateToken(String username) {
    var authAccount =
        authAccountRepository
            .findByUserUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

    return jwtService.generateToken(authAccount);
  }

  @Transactional(readOnly = true)
  public String generateToken(AuthAccount authAccount) {
    return jwtService.generateToken(authAccount);
  }
}
