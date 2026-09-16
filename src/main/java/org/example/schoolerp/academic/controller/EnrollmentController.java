package org.example.schoolerp.academic.controller;

import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.schoolerp.academic.dto.CreateEnrollmentRequest;
import org.example.schoolerp.academic.dto.EnrollmentResponse;
import org.example.schoolerp.academic.dto.UpdateEnrollmentRequest;
import org.example.schoolerp.academic.service.EnrollmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

  private final EnrollmentService enrollmentService;

  @PostMapping
  public ResponseEntity<EnrollmentResponse> create(
      @Valid @RequestBody CreateEnrollmentRequest entity) {
    var enrollment = enrollmentService.create(entity);
    return ResponseEntity.status(HttpStatus.CREATED).body(enrollment);
  }

  @GetMapping
  public ResponseEntity<List<EnrollmentResponse>> getAll(
      @RequestParam(required = false) UUID classSectionId,
      @RequestParam(required = false) UUID academicYearId,
      @RequestParam(required = false) UUID academicClassId,
      @RequestParam(required = false) Instant from,
      @RequestParam(required = false) Instant to) {

    String queryType =
        classSectionId != null
            ? "section"
            : academicYearId != null
                ? "year"
                : academicClassId != null ? "class" : from != null || to != null ? "date" : "all";

    var enrollments =
        switch (queryType) {
          case "section" -> enrollmentService.getAllByClassSection(classSectionId);
          case "year" -> enrollmentService.getAllByAcademicYear(academicYearId);
          case "class" -> enrollmentService.getAllByAcademicClass(academicClassId);
          case "date" -> enrollmentService.getAllByDateRange(from, to);
          case "all" -> enrollmentService.getAll();
          default -> throw new IllegalStateException("Unexpected query type: " + queryType);
        };

    return ResponseEntity.ok(enrollments);
  }

  @GetMapping("/{id}")
  public ResponseEntity<EnrollmentResponse> get(@PathVariable UUID id) {
    var enrollment = enrollmentService.get(id);
    return ResponseEntity.ok(enrollment);
  }

  @PutMapping("/{id}")
  public ResponseEntity<EnrollmentResponse> update(
      @PathVariable UUID id, @Valid @RequestBody UpdateEnrollmentRequest entity) {
    var enrollment = enrollmentService.update(id, entity);
    return ResponseEntity.ok(enrollment);
  }

  /** NOTE: delete endpoints is on halt */
  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete() {
    return null;
  }
}
