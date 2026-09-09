package org.example.schoolerp.academic.repo;

import java.util.UUID;
import org.example.schoolerp.academic.entity.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademicYearRepository extends JpaRepository<AcademicYear, UUID> {}
