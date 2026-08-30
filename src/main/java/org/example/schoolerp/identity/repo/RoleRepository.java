package org.example.schoolerp.identity.repo;

import java.util.Optional;
import java.util.UUID;
import org.example.schoolerp.identity.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, UUID> {
  Optional<Role> findByName(String name);
}
