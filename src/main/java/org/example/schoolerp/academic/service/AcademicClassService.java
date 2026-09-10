package org.example.schoolerp.academic.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.schoolerp.academic.dto.AcademicClassResponse;
import org.example.schoolerp.academic.dto.AcademicClassWithSectionsResponse;
import org.example.schoolerp.academic.dto.ClassSectionResponse;
import org.example.schoolerp.academic.dto.ClassSectionSummaryResponse;
import org.example.schoolerp.academic.dto.CreateClassSectionRequest;
import org.example.schoolerp.academic.entity.AcademicClass;
import org.example.schoolerp.academic.entity.ClassSection;
import org.example.schoolerp.academic.repo.AcademicClassRepository;
import org.example.schoolerp.academic.repo.AcademicYearRepository;
import org.example.schoolerp.academic.repo.ClassSectionRepository;
import org.example.schoolerp.staff.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AcademicClassService {

  private final AcademicClassRepository academicClassRepository;
  private final ClassSectionRepository classSectionRepository;
  private final AcademicYearRepository academicYearRepository;
  private final TeacherRepository teacherRepository;

  @Transactional
  public AcademicClassResponse createClass(String name) {
    var academicClass = academicClassRepository.save(new AcademicClass(name));

    return new AcademicClassResponse(academicClass);
  }

  @SuppressWarnings("null")
  @Transactional(readOnly = true)
  public List<AcademicClassWithSectionsResponse> getClassesWithSections() {
    return classSectionRepository.findAll().stream()
        .collect(Collectors.groupingBy(ClassSection::getAcademicClass))
        .entrySet()
        .stream()
        .map(
            entry -> {
              var academicClass = entry.getKey();
              var sections = entry.getValue();

              return new AcademicClassWithSectionsResponse(
                  academicClass.getId(),
                  academicClass.getName(),
                  sections.stream().map(ClassSectionSummaryResponse::new).toList());
            })
        .toList();
  }

  @Transactional(readOnly = true)
  public List<ClassSectionResponse> getclassesWithSectionByClassId(UUID id) {
    return classSectionRepository.findByAcademicClassId(id).stream()
        .map(ClassSectionResponse::new)
        .toList();
  }

  @Transactional
  public ClassSectionResponse createSection(UUID classId, CreateClassSectionRequest request) {
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
