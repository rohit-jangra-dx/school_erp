package org.example.schoolerp.identity;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, UUID>{
    boolean existsByName(String name);    
}
