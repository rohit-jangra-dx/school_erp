package org.example.schoolerp.identity.service;

import lombok.RequiredArgsConstructor;
import org.example.schoolerp.identity.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterationService {

  private final AuthService authService;
  private final AuthAccountService authAccountService;
  private final UserService userService;

  public record RegisterationResult(String username, String token) {}

  @Transactional
  public User registerUser(String username, String password, String roleName) {
    var user = userService.create(username, roleName);
    authAccountService.create(user, password);

    return user;
  }

  @Transactional
  public RegisterationResult registerAndAuthenticate(
      String username, String password, String roleName) {
    var user = userService.create(username, roleName);
    var authAccount = authAccountService.create(user, password);

    var token = authService.generateToken(authAccount);

    return new RegisterationResult(user.getUsername(), token);
  }
}
