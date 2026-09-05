package org.example.schoolerp.staff;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, UUID>{
    
}
