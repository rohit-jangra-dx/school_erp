package org.example.schoolerp.academic.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.schoolerp.academic.dto.CreateEnrollmentRequest;
import org.example.schoolerp.academic.dto.EnrollmentResponse;
import org.example.schoolerp.academic.dto.UpdateEnrollmentRequest;
import org.example.schoolerp.academic.entity.ClassSection;
import org.example.schoolerp.academic.entity.Enrollment;
import org.example.schoolerp.academic.exceptions.ClassSectionNotFoundException;
import org.example.schoolerp.academic.exceptions.EnrollmentNotFoundException;
import org.example.schoolerp.academic.exceptions.SectionCapacityExceededException;
import org.example.schoolerp.academic.repo.ClassSectionRepository;
import org.example.schoolerp.academic.repo.EnrollmentRepository;
import org.example.schoolerp.core.InvalidQueryParameterException;
import org.example.schoolerp.student.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * NOTE: it's universal in current system that we need domain exceptions instead of java basic ones.
 */
@Service
@RequiredArgsConstructor
public class EnrollmentService {

  private final EnrollmentRepository enrollmentRepository;
  private final StudentRepository studentRepository;
  private final ClassSectionRepository classSectionRepository;

  @Transactional
  public EnrollmentResponse create(CreateEnrollmentRequest request) {

    var classSection =
        classSectionRepository
            .findById(request.getClassSectionId())
            .orElseThrow(
                () ->
                    new ClassSectionNotFoundException(
                        "Class section not found: " + request.getClassSectionId()));

    var academicYear = classSection.getAcademicYear();
    var student =
        studentRepository
            .findById(request.getStudentId())
            .orElseThrow(
                () -> new IllegalArgumentException("Student not found: " + request.getStudentId()));

    var enrollment = new Enrollment(academicYear, classSection, student, request.getRollNo());

    enrollmentRepository.save(enrollment);
    return new EnrollmentResponse(enrollment);
  }

  @Transactional(readOnly = true)
  public List<EnrollmentResponse> getAll() {
    return enrollmentRepository.findAll().stream().map(EnrollmentResponse::new).toList();
  }

  @Transactional(readOnly = true)
  public List<EnrollmentResponse> getAllByClassSection(UUID classSectionId) {
    return enrollmentRepository.findByClassSectionId(classSectionId).stream()
        .map(EnrollmentResponse::new)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<EnrollmentResponse> getAllByAcademicYear(UUID academicYearId) {
    return enrollmentRepository.findByAcademicYearId(academicYearId).stream()
        .map(EnrollmentResponse::new)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<EnrollmentResponse> getAllByDateRange(Instant from, Instant to) {
    // from must be smaller than to
    if (from != null && to != null && from.isAfter(to)) {
      throw new InvalidQueryParameterException(
          "From %s must not be after to %s".formatted(from, to));
    }

    return enrollmentRepository.findByCreatedAtBetween(from, to).stream()
        .map(EnrollmentResponse::new)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<EnrollmentResponse> getAllByAcademicClass(UUID academicClassId) {
    return enrollmentRepository.findByClassSectionAcademicClassId(academicClassId).stream()
        .map(EnrollmentResponse::new)
        .toList();
  }

  @Transactional(readOnly = true)
  public EnrollmentResponse get(UUID id) {
    var enrollment =
        enrollmentRepository
            .findById(id)
            .orElseThrow(() -> new EnrollmentNotFoundException("Enrollment not found: " + id));
    return new EnrollmentResponse(enrollment);
  }

  @Transactional
  public EnrollmentResponse update(UUID id, UpdateEnrollmentRequest request) {
    var enrollment =
        enrollmentRepository
            .findById(id)
            .orElseThrow(() -> new EnrollmentNotFoundException("Enrollment not found: " + id));

    var oldSection = enrollment.getClassSection();

    if (request.getClassSectionId() == null
        || oldSection.getId().equals(request.getClassSectionId())) {
      return new EnrollmentResponse(enrollment);
    }

    UUID newSectionId = request.getClassSectionId();

    @SuppressWarnings("unused")
    ClassSection sourceSection;
    ClassSection destinationSection;

    // deterministic order locking
    if (oldSection.getId().compareTo(newSectionId) < 0) {
      sourceSection = classSectionRepository.findByIdForUpdate(oldSection.getId()).orElseThrow();

      destinationSection = classSectionRepository.findByIdForUpdate(newSectionId).orElseThrow();
    } else {
      destinationSection = classSectionRepository.findByIdForUpdate(newSectionId).orElseThrow();

      sourceSection = classSectionRepository.findByIdForUpdate(oldSection.getId()).orElseThrow();
    }

    moveEnrollment(enrollment, destinationSection);

    return new EnrollmentResponse(enrollment);
  }

  private void moveEnrollment(Enrollment enrollment, ClassSection newSection) {
    long currentCount = enrollmentRepository.countByClassSectionId(newSection.getId());

    if (currentCount >= newSection.getCapacity()) {
      throw new SectionCapacityExceededException("Class section is full: " + newSection.getId());
    }

    int nextRollNo =
        enrollmentRepository.findMaxRollNoByClassSectionId(newSection.getId()).orElse(0) + 1;
    enrollment.setClassSection(newSection);
    enrollment.setRollNo(nextRollNo);
  }
}
