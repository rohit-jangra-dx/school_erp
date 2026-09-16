package org.example.schoolerp.academic.repo;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.schoolerp.academic.entity.ClassSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClassSectionRepository extends JpaRepository<ClassSection, UUID> {
  List<ClassSection> findByAcademicClassId(UUID classId);

  /**
   * it's to force serialization for enrollment updations (where roll no gets updated by
   * incrementing the max yet.)
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
      SELECT c
      FROM ClassSection c
      WHERE c.id = :id
      """)
  Optional<ClassSection> findByIdForUpdate(@Param("id") UUID id);
}
