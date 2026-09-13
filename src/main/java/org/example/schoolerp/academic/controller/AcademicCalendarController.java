package org.example.schoolerp.academic.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.schoolerp.academic.dto.AcademicYearResponse;
import org.example.schoolerp.academic.dto.CreateAcademicCalendarRequest;
import org.example.schoolerp.academic.service.AcademicCalendarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/academic-calendars")
@RequiredArgsConstructor
public class AcademicCalendarController {

  private final AcademicCalendarService academicCalendarService;

  @PostMapping
  public ResponseEntity<AcademicYearResponse> createCalendar(
      @Valid @RequestBody CreateAcademicCalendarRequest entity) {
    var year = academicCalendarService.createCalendar(entity.getStartDate(), entity.getEndDate());
    return ResponseEntity.status(HttpStatus.CREATED).body(year);
  }

  @GetMapping
  public ResponseEntity<List<AcademicYearResponse>> getCalendars() {
    var years = academicCalendarService.getAllAcademicYears();
    return ResponseEntity.ok(years);
  }

  @GetMapping("/{id}")
  public ResponseEntity<AcademicYearResponse> getMethodName(@PathVariable UUID id) {
    var year = academicCalendarService.getAcademicYear(id);
    return ResponseEntity.ok(year);
  }

  /**
   * NOTE: deleting resources is on halt. I don't know what stratedgy i will use, this is for
   * future.
   */
  @DeleteMapping("/{id}")
  public String deleteCalendar(@PathVariable UUID id) {
    return "";
  }
}
