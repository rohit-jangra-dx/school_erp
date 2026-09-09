package org.example.schoolerp.academic.repo;

import java.util.UUID;
import org.example.schoolerp.academic.entity.AcademicClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademicClassRepository extends JpaRepository<AcademicClass, UUID> {}
