package org.example.schoolerp.identity.service;

import lombok.RequiredArgsConstructor;
import org.example.schoolerp.identity.entity.AuthAccount;
import org.example.schoolerp.identity.entity.User;
import org.example.schoolerp.identity.repo.AuthAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthAccountService {
  private final PasswordEncoder passwordEncoder;
  private final AuthAccountRepository authAccountRepository;

  @Transactional
  public AuthAccount create(User user, String rawPassword) {
    return authAccountRepository.save(new AuthAccount(user, passwordEncoder.encode(rawPassword)));
  }
}
