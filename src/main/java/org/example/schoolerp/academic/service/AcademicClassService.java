package org.example.schoolerp.academic.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.schoolerp.academic.dto.AcademicClassResponse;
import org.example.schoolerp.academic.dto.AcademicClassWithSectionsResponse;
import org.example.schoolerp.academic.dto.ClassSectionSummaryResponse;
import org.example.schoolerp.academic.dto.CreateAcademicClassRequest;
import org.example.schoolerp.academic.dto.UpdateAcademicClassRequest;
import org.example.schoolerp.academic.entity.AcademicClass;
import org.example.schoolerp.academic.entity.ClassSection;
import org.example.schoolerp.academic.repo.AcademicClassRepository;
import org.example.schoolerp.academic.repo.ClassSectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AcademicClassService {

  private final AcademicClassRepository academicClassRepository;
  private final ClassSectionRepository classSectionRepository;

  @Transactional
  public AcademicClassWithSectionsResponse create(CreateAcademicClassRequest request) {
    var academicClass = academicClassRepository.save(new AcademicClass(request.getName()));

    return new AcademicClassWithSectionsResponse(
        academicClass, new ArrayList<ClassSectionSummaryResponse>());
  }

  @SuppressWarnings("null")
  @Transactional(readOnly = true)
  public List<AcademicClassWithSectionsResponse> getAll() {
    return classSectionRepository.findAll().stream()
        .collect(Collectors.groupingBy(ClassSection::getAcademicClass))
        .entrySet()
        .stream()
        .map(
            entry -> {
              var academicClass = entry.getKey();
              var sections = entry.getValue();

              return new AcademicClassWithSectionsResponse(
                  academicClass, sections.stream().map(ClassSectionSummaryResponse::new).toList());
            })
        .toList();
  }

  @Transactional(readOnly = true)
  public AcademicClassWithSectionsResponse getSections(UUID id) {
    var academicClass =
        academicClassRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Class not found: " + id));

    var sections =
        classSectionRepository.findByAcademicClassId(id).stream()
            .map(ClassSectionSummaryResponse::new)
            .toList();

    return new AcademicClassWithSectionsResponse(academicClass, sections);
  }

  @Transactional
  public AcademicClassResponse update(UUID id, UpdateAcademicClassRequest request) {
    var academicClass =
        academicClassRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Class not found: " + id));

    if (request.getName() != null) {
      academicClass.setName(request.getName());
    }

    return new AcademicClassResponse(academicClass);
  }
}
