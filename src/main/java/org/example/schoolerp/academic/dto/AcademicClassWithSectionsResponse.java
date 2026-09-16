package org.example.schoolerp.academic.dto;

import java.util.List;
import java.util.UUID;
import org.example.schoolerp.academic.entity.AcademicClass;

public record AcademicClassWithSectionsResponse(
    UUID id, String name, List<ClassSectionSummaryResponse> sections) {
  public AcademicClassWithSectionsResponse(
      AcademicClass academicClass, List<ClassSectionSummaryResponse> sections) {
    this(academicClass.getId(), academicClass.getName(), sections);
  }
}
