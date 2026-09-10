package org.example.schoolerp.academic;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.schoolerp.academic.dto.AcademicClassResponse;
import org.example.schoolerp.academic.dto.AcademicClassWithSectionsResponse;
import org.example.schoolerp.academic.dto.ClassSectionResponse;
import org.example.schoolerp.academic.dto.CreateAcademicClassRequest;
import org.example.schoolerp.academic.dto.CreateClassSectionRequest;
import org.example.schoolerp.academic.service.AcademicClassService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/academic-classes")
@RequiredArgsConstructor
public class AcademicClassSectionController {

  private final AcademicClassService academicClassService;

  @PostMapping
  public ResponseEntity<AcademicClassResponse> createClass(
      @Valid @RequestBody CreateAcademicClassRequest entity) {
    var result = academicClassService.createClass(entity.getName());
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }

  @GetMapping
  public ResponseEntity<List<AcademicClassWithSectionsResponse>> getClasses() {
    var result = academicClassService.getClassesWithSections();
    return ResponseEntity.ok(result);
  }

  @GetMapping("/{id}")
  public ResponseEntity<List<ClassSectionResponse>> getMethodName(@PathVariable UUID id) {
    var result = academicClassService.getclassesWithSectionByClassId(id);
    return ResponseEntity.ok(result);
  }

  @PostMapping("/{id}/sections")
  public ResponseEntity<ClassSectionResponse> createClassSections(
      @PathVariable UUID id, @Valid @RequestBody CreateClassSectionRequest entity) {
    var result = academicClassService.createSection(id, entity);
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }
}
