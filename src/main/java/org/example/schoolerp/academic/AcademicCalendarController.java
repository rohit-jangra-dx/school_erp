package org.example.schoolerp.academic;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.example.schoolerp.academic.dto.AcademicDayResponse;
import org.example.schoolerp.academic.dto.CreateAcademicCalendarRequest;
import org.example.schoolerp.academic.dto.CreateAcademicCalendarResponse;
import org.example.schoolerp.academic.service.AcademicCalendarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/academic-calendars")
public class AcademicCalendarController {

  private final AcademicCalendarService academicCalendarService;

  AcademicCalendarController(AcademicCalendarService academicCalendarService) {
    this.academicCalendarService = academicCalendarService;
  }

  @PostMapping
  public ResponseEntity<CreateAcademicCalendarResponse> createCalendar(
      @Valid @RequestBody CreateAcademicCalendarRequest entity) {
    var response =
        academicCalendarService.createCalendar(entity.getStartDate(), entity.getEndDate());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  // to get full calendar
  @GetMapping("/{id}")
  public ResponseEntity<List<AcademicDayResponse>> getCalendarById(@PathVariable UUID id) {

    var days = academicCalendarService.getCalendarDaysById(id);
    return ResponseEntity.ok(days);
  }

  // by date range
  @GetMapping("/{id}/days")
  public ResponseEntity<List<AcademicDayResponse>> getCalendarByDateRange(
      @PathVariable UUID id, @RequestParam LocalDate from, @RequestParam LocalDate to) {

    var days = academicCalendarService.getCalendarByDaysByDateRange(id, from, to);
    return ResponseEntity.ok(days);
  }
}
