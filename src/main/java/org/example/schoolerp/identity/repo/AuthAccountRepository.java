package org.example.schoolerp.identity.repo;

import java.util.Optional;
import org.example.schoolerp.identity.entity.AuthAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthAccountRepository extends JpaRepository<AuthAccount, Long> {

  Optional<AuthAccount> findByUserUsername(String username);

  boolean existsByUserUsername(String username);
}
