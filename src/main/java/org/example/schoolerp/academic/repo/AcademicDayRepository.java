package org.example.schoolerp.academic.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.schoolerp.academic.entity.AcademicDay;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * NOTE: Hibernate allows projection, though this could be pain, but it still will be better that
 * "SELECT * " kinda queries, every call is doing that, but it's for later time.
 */
public interface AcademicDayRepository extends JpaRepository<AcademicDay, UUID> {
  Optional<AcademicDay> findByAcademicYearIdAndDate(UUID academicYearId, LocalDate date);

  List<AcademicDay> findByAcademicYearId(UUID academicYearId);

  List<AcademicDay> findByAcademicYearIdAndDateBetween(
      UUID academicYearId, LocalDate start, LocalDate end);
}
