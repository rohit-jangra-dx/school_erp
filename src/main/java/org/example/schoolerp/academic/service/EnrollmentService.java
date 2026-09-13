package org.example.schoolerp.academic.service;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.schoolerp.academic.entity.Enrollment;
import org.example.schoolerp.academic.repo.AcademicYearRepository;
import org.example.schoolerp.academic.repo.ClassSectionRepository;
import org.example.schoolerp.academic.repo.EnrollmentRepository;
import org.example.schoolerp.student.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * NOTE: it's universal in current system that we need domain exceptions instead of java basic ones.
 * TODO: add domain exceptions (for all the domains)
 */
@Service
@RequiredArgsConstructor
public class EnrollmentService {

  private final EnrollmentRepository enrollmentRepository;
  private StudentRepository studentRepository;
  private ClassSectionRepository classSectionRepository;
  private AcademicYearRepository academicYearRepository;

  @Data
  public class CreateEnrollmentRequest {
    @NotNull private UUID academicYearId;
    @NotNull private UUID classSectionId;
    @NotNull private UUID studentId;
    @NotNull private Integer rollNo;
  }

  // FIXME: make sure classSection's year is similar to the year provided to avoid illegal state
  // enrollment
  // NOTE: same will come in attendance where u would be getting enrollment and year, solution is
  // repo ....
  @Transactional
  public void CreateEnrollment(CreateEnrollmentRequest request) {
    var academicYear =
        academicYearRepository
            .findById(request.getAcademicYearId())
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Academic year not found: " + request.getAcademicYearId()));

    var classSection =
        classSectionRepository
            .findById(request.getClassSectionId())
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Class section not found: " + request.getClassSectionId()));

    var student =
        studentRepository
            .findById(request.getStudentId())
            .orElseThrow(
                () -> new IllegalArgumentException("Student not found: " + request.getStudentId()));

    var enrollment = new Enrollment(academicYear, classSection, student, request.getRollNo());

    enrollmentRepository.save(enrollment);
  }
}
