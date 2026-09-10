package org.example.schoolerp.staff;

import java.util.UUID;

public record TeacherResponse(UUID id, String username) {
  public TeacherResponse(Teacher teacher) {
    this(teacher.getId(), teacher.getUser().getUsername());
  }
}
