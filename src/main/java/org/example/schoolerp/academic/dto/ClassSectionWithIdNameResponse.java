package org.example.schoolerp.academic.dto;

import java.util.UUID;
import org.example.schoolerp.academic.entity.ClassSection;

public record ClassSectionWithIdNameResponse(UUID id, String name) {
  public ClassSectionWithIdNameResponse(ClassSection section) {
    this(section.getId(), section.getName());
  }
}
