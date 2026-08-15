package org.example.schoolerp.identity.repos;

import java.util.UUID;

import org.example.schoolerp.identity.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, UUID>{
    boolean existsByName(String name);    
}
