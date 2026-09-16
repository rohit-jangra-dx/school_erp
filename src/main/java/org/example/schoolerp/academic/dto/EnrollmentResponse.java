package org.example.schoolerp.academic.dto;

import java.util.UUID;
import org.example.schoolerp.academic.entity.Enrollment;

public record EnrollmentResponse(
    UUID id,
    UUID studentId,
    AcademicYearResponse academicYear,
    ClassSectionWithIdNameResponse classSection,
    Integer rollNo) {
  public EnrollmentResponse(Enrollment enrollment) {
    this(
        enrollment.getId(),
        enrollment.getStudent().getId(),
        new AcademicYearResponse(enrollment.getAcademicYear()),
        new ClassSectionWithIdNameResponse(enrollment.getClassSection()),
        enrollment.getRollNo());
  }
}
