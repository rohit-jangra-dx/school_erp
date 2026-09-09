package org.example.schoolerp.academic.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.example.schoolerp.academic.entity.AcademicDay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademicDayRepository extends JpaRepository<AcademicDay, UUID> {
  List<AcademicDay> findByAcademicYearId(UUID academicYearId);

  List<AcademicDay> findByAcademicYearIdAndDateBetween(
      UUID academicYearId, LocalDate start, LocalDate end);
}
