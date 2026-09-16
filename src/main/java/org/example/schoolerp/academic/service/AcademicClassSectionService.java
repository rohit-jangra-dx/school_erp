package org.example.schoolerp.academic.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.schoolerp.academic.dto.ClassSectionResponse;
import org.example.schoolerp.academic.dto.CreateClassSectionRequest;
import org.example.schoolerp.academic.dto.UpdateClassSectionRequest;
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

  @Transactional(readOnly = true)
  public List<ClassSectionResponse> getall(UUID classId) {
    return classSectionRepository.findByAcademicClassId(classId).stream()
        .map(ClassSectionResponse::new)
        .toList();
  }

  @Transactional(readOnly = true)
  public ClassSectionResponse get(UUID id) {
    var section =
        classSectionRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Class section not found: " + id));

    return new ClassSectionResponse(section);
  }

  @Transactional
  public ClassSectionResponse update(UUID id, UpdateClassSectionRequest request) {
    var section =
        classSectionRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Class section not found: " + id));

    if (request.getName() != null) {
      section.setName(request.getName());
    }
    if (request.getRoom() != null) {
      section.setRoom(request.getRoom());
    }
    if (request.getCapacity() != null) {
      section.setCapacity(request.getCapacity());
    }
    if (request.getTeacherId() != null) {
      var teacher =
          teacherRepository
              .findById(request.getTeacherId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException("Teacher not found: " + request.getTeacherId()));
      section.setTeacher(teacher);
    }

    return new ClassSectionResponse(section);
  }
}
