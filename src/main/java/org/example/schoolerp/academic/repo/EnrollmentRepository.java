package org.example.schoolerp.academic.repo;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.schoolerp.academic.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
  List<Enrollment> findByClassSectionId(UUID classSectionId);

  List<Enrollment> findByClassSectionAcademicClassId(UUID academicClassId);

  List<Enrollment> findByAcademicYearId(UUID academicyearId);

  @Query(
      """
      SELECT e from Enrollment e
      WHERE (:from IS NULL OR e.createdAt >= :from)
       AND (:to IS NULL OR e.createdAt <= :to)
      """)
  List<Enrollment> findByCreatedAtBetween(@Param("from") Instant from, @Param("to") Instant to);

  @Query(
      """
      SELECT MAX(e.rollNo)
      FROM ENROLLMENT e
      WHERE e.classSection.id = :classSectionId
      """)
  Optional<Integer> findMaxRollNoByClassSectionId(@Param("classSectionId") UUID classSectionId);

  Long countByClassSectionId(UUID classSectionId);
}
