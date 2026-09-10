package org.example.schoolerp.academic.dto;

import java.util.UUID;
import org.example.schoolerp.academic.entity.ClassSection;
import org.example.schoolerp.staff.TeacherResponse;

public record ClassSectionResponse(
    UUID id,
    AcademicClassResponse academicClass,
    AcademicYearResponse academicYear,
    TeacherResponse teacherResponse,
    String name,
    Integer room,
    Integer capacity) {

  public ClassSectionResponse(ClassSection section) {
    this(
        section.getId(),
        new AcademicClassResponse(section.getAcademicClass()),
        new AcademicYearResponse(section.getAcademicYear()),
        new TeacherResponse(section.getTeacher()),
        section.getName(),
        section.getRoom(),
        section.getCapacity());
  }
}
