package org.example.schoolerp.academic.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.schoolerp.academic.dto.ClassSectionResponse;
import org.example.schoolerp.academic.dto.CreateClassSectionRequest;
import org.example.schoolerp.academic.dto.UpdateClassSectionRequest;
import org.example.schoolerp.academic.service.AcademicClassSectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/academic-classes")
@RequiredArgsConstructor
public class AcademicClassSectionController {

  private final AcademicClassSectionService academicClassSectionService;

  @PostMapping("/{id}/sections")
  public ResponseEntity<ClassSectionResponse> create(
      @PathVariable UUID id, @Valid @RequestBody CreateClassSectionRequest entity) {
    var result = academicClassSectionService.create(id, entity);
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }

  @GetMapping("/{id}/sections")
  public ResponseEntity<List<ClassSectionResponse>> getAll(@PathVariable UUID classId) {
    var sections = academicClassSectionService.getall(classId);
    return ResponseEntity.ok(sections);
  }

  @GetMapping("/sections/{id}")
  public ResponseEntity<ClassSectionResponse> get(@PathVariable UUID id) {
    var section = academicClassSectionService.get(id);
    return ResponseEntity.ok(section);
  }

  @PutMapping("/sections/{id}")
  public ResponseEntity<ClassSectionResponse> update(
      @PathVariable UUID id, @RequestBody UpdateClassSectionRequest entity) {
    var section = academicClassSectionService.update(id, entity);
    return ResponseEntity.ok(section);
  }

  /** NOTE: Delete endpoints are on hold for now. */
  @DeleteMapping("/sections/{id}")
  public ResponseEntity<?> delete() {
    return null;
  }
}
