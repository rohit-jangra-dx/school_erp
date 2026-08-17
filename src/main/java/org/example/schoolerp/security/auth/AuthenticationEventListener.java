package org.example.schoolerp.security.auth;

import org.example.schoolerp.identity.repos.AuthAccountRepository;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * 
 * AuthenticationEventListener contains listeners to record success and failure attempts on authAccount.
 */
@Component
@RequiredArgsConstructor
public class AuthenticationEventListener {

    private final AuthAccountRepository authAccountRepository;

    @EventListener
    public void onFailure(AuthenticationFailureBadCredentialsEvent event) {
        String username = event.getAuthentication().getName();
        authAccountRepository.findByUserUsername(username).ifPresent(acc -> {
            acc.recordFailedAttempt();
            authAccountRepository.save(acc);
        });
    }

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        authAccountRepository.findByUserUsername(username).ifPresent(acc -> {
            acc.recordSuccess();
            authAccountRepository.save(acc);
        });
    }
}
