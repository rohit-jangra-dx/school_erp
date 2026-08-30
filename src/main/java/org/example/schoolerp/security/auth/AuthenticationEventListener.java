package org.example.schoolerp.security.auth;

import lombok.RequiredArgsConstructor;
import org.example.schoolerp.identity.repo.AuthAccountRepository;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * AuthenticationEventListener contains listeners to record success and failure attempts on
 * authAccount.
 */
@Component
@RequiredArgsConstructor
public class AuthenticationEventListener {

  private final AuthAccountRepository authAccountRepository;

  @EventListener
  public void onFailure(AuthenticationFailureBadCredentialsEvent event) {
    String username = event.getAuthentication().getName();
    authAccountRepository
        .findByUserUsername(username)
        .ifPresent(
            acc -> {
              acc.recordFailedAttempt();
              authAccountRepository.save(acc);
            });
  }

  @EventListener
  public void onSuccess(AuthenticationSuccessEvent event) {
    String username = event.getAuthentication().getName();
    authAccountRepository
        .findByUserUsername(username)
        .ifPresent(
            acc -> {
              acc.recordSuccess();
              authAccountRepository.save(acc);
            });
  }
}
