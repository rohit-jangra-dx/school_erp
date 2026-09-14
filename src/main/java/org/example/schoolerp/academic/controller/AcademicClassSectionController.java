package org.example.schoolerp.academic.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.schoolerp.academic.dto.ClassSectionResponse;
import org.example.schoolerp.academic.dto.CreateClassSectionRequest;
import org.example.schoolerp.academic.service.AcademicClassSectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/academic-classes")
@RequiredArgsConstructor
public class AcademicClassSectionController {

  private final AcademicClassSectionService academicClassSectionService;

  @PostMapping("/{id}/sections")
  public ResponseEntity<ClassSectionResponse> createClassSections(
      @PathVariable UUID id, @Valid @RequestBody CreateClassSectionRequest entity) {
    var result = academicClassSectionService.create(id, entity);
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }
}
