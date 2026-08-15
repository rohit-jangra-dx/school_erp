package org.example.schoolerp.identity.entity;

import java.time.Duration;
import java.time.Instant;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "auth_accounts")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class AuthAccount {
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String passwordHash;

    private int failedAttempts = 0;
    private Instant lockedUntil;

    public  AuthAccount(User user,String passwordHash) {
        this.user = user;
        this.passwordHash = passwordHash;
    }

    public void recordFailedAttempt() {
        failedAttempts++;
        // TODO: Add property for MaxFailedAttempts
        if (failedAttempts >= 5) {
            //TODO: Add the property for lockedUntil
            lockedUntil = Instant.now().plus(Duration.ofMinutes(15));
        }
    }

    public void recordSuccess() {
        failedAttempts = 0;
        lockedUntil = null;
    }

    public boolean isLocked() {
        return lockedUntil != null && Instant.now().isBefore(lockedUntil);
    }
}
