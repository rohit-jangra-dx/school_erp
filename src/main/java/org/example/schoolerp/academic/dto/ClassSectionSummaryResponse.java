package org.example.schoolerp.academic.dto;

import java.util.UUID;
import org.example.schoolerp.academic.entity.ClassSection;
import org.example.schoolerp.staff.TeacherResponse;

public record ClassSectionSummaryResponse(
    UUID id,
    AcademicYearResponse academicYear,
    TeacherResponse teacher,
    String name,
    Integer room,
    Integer capacity) {
  public ClassSectionSummaryResponse(ClassSection section) {
    this(
        section.getId(),
        new AcademicYearResponse(section.getAcademicYear()),
        new TeacherResponse(section.getTeacher()),
        section.getName(),
        section.getRoom(),
        section.getCapacity());
  }
}
