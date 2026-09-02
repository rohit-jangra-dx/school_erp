package org.example.schoolerp.student;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuardianRepository extends JpaRepository<Guardian, UUID> {
  Optional<Guardian> findByEmail(String email);

  Optional<Guardian> findByEmailAndRelation(String email, String relation);
}
