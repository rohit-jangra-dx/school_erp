package org.example.schoolerp.identity.repos;

import java.util.UUID;

import org.example.schoolerp.identity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID>{
    
}
