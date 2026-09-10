package org.example.schoolerp.academic.dto;

import java.util.UUID;
import org.example.schoolerp.academic.entity.AcademicClass;

public record AcademicClassResponse(UUID id, String name) {
  public AcademicClassResponse(AcademicClass academicClass) {
    this(academicClass.getId(), academicClass.getName());
  }
}
