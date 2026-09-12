package org.example.schoolerp.academic.repo;

import java.util.UUID;
import org.example.schoolerp.academic.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {}
