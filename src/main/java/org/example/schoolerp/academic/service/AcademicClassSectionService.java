package org.example.schoolerp.academic.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.schoolerp.academic.dto.ClassSectionResponse;
import org.example.schoolerp.academic.dto.CreateClassSectionRequest;
import org.example.schoolerp.academic.entity.ClassSection;
import org.example.schoolerp.academic.repo.AcademicClassRepository;
import org.example.schoolerp.academic.repo.AcademicYearRepository;
import org.example.schoolerp.academic.repo.ClassSectionRepository;
import org.example.schoolerp.staff.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AcademicClassSectionService {

  private final AcademicYearRepository academicYearRepository;
  private final AcademicClassRepository academicClassRepository;
  private final TeacherRepository teacherRepository;
  private final ClassSectionRepository classSectionRepository;

  @Transactional
  public ClassSectionResponse create(UUID classId, CreateClassSectionRequest request) {
    var academicYear =
        academicYearRepository
            .findById(request.getAcademicYearId())
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Academic Year not found: " + request.getAcademicYearId()));

    var academicClass =
        academicClassRepository
            .findById(classId)
            .orElseThrow(
                () -> new IllegalArgumentException("Academic Class not found: " + classId));

    var teacher =
        teacherRepository
            .findById(request.getTeacherId())
            .orElseThrow(
                () -> new IllegalArgumentException("Teacher not found: " + request.getTeacherId()));

    var classSection =
        new ClassSection(
            academicClass,
            academicYear,
            teacher,
            request.getName(),
            request.getRoom(),
            request.getCapacity());
    classSectionRepository.save(classSection);

    return new ClassSectionResponse(classSection);
  }
}
