package org.example.schoolerp.academic.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.schoolerp.academic.dto.AcademicClassResponse;
import org.example.schoolerp.academic.dto.AcademicClassWithSectionsResponse;
import org.example.schoolerp.academic.dto.CreateAcademicClassRequest;
import org.example.schoolerp.academic.dto.UpdateAcademicClassRequest;
import org.example.schoolerp.academic.service.AcademicClassService;
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
public class AcademicClassController {
  private final AcademicClassService academicClassService;

  @PostMapping
  public ResponseEntity<AcademicClassWithSectionsResponse> create(
      @Valid @RequestBody CreateAcademicClassRequest entity) {
    var academicClass = academicClassService.create(entity);
    return ResponseEntity.status(HttpStatus.CREATED).body(academicClass);
  }

  @GetMapping
  public ResponseEntity<List<AcademicClassWithSectionsResponse>> getAll() {
    var acadmicClasses = academicClassService.getAll();
    return ResponseEntity.ok(acadmicClasses);
  }

  @GetMapping("/{id}")
  public ResponseEntity<AcademicClassWithSectionsResponse> get(@PathVariable UUID id) {
    var academicClass = academicClassService.getSections(id);
    return ResponseEntity.ok(academicClass);
  }

  @PutMapping("/{id}")
  public ResponseEntity<AcademicClassResponse> update(
      @PathVariable UUID id, @Valid @RequestBody UpdateAcademicClassRequest entity) {
    var updatedAcademicClass = academicClassService.update(id, entity);
    return ResponseEntity.ok(updatedAcademicClass);
  }

  /** NOTE: not implemented intentionally, to be done after all core structures are on place */
  @DeleteMapping()
  public ResponseEntity<?> deleteClass() {
    return null;
  }
}
