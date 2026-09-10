package org.example.schoolerp.academic.repo;

import java.util.List;
import java.util.UUID;
import org.example.schoolerp.academic.entity.ClassSection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassSectionRepository extends JpaRepository<ClassSection, UUID> {
  List<ClassSection> findByAcademicClassId(UUID classId);
}
