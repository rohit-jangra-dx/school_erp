package org.example.schoolerp.academic.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.schoolerp.academic.dto.AcademicDayResponse;
import org.example.schoolerp.academic.dto.UpdateAcademicDayRequest;
import org.example.schoolerp.academic.service.AcademicDayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/academic-calendars")
@RequiredArgsConstructor
public class AcademicCalendarDays {

  private final AcademicDayService academicDayService;

  @GetMapping("/{id}/days")
  public ResponseEntity<List<AcademicDayResponse>> getCalendarDaysByCalendarId(
      @PathVariable UUID id) {
    var calendarDays = academicDayService.getAcademicDays(id);

    return ResponseEntity.ok(calendarDays);
  }

  @GetMapping("/{id}/days")
  public ResponseEntity<List<AcademicDayResponse>> getMethodName(
      @PathVariable UUID id, @RequestParam LocalDate from, @RequestParam LocalDate to) {
    var calendarDays = academicDayService.getAcademicDays(id, from, to);

    return ResponseEntity.ok(calendarDays);
  }

  /** NOTE: id not needed since i req body itself */
  @PutMapping("/{id}/days")
  public ResponseEntity<List<AcademicDayResponse>> updateCalendarDays(
      @PathVariable UUID id, @RequestBody List<UpdateAcademicDayRequest> entities) {
    var updatedDays = academicDayService.updateAcademicDays(id, entities);

    return ResponseEntity.ok(updatedDays);
  }

  /** useless calendar id, day can be fetched even without it */
  @GetMapping("/days/{id}")
  public ResponseEntity<AcademicDayResponse> getCalendarDay(@RequestParam UUID id) {
    var day = academicDayService.getAcademicDay(id);

    return ResponseEntity.ok(day);
  }

  @PutMapping("/days/{id}")
  public ResponseEntity<AcademicDayResponse> updateCalendarDay(
      @PathVariable UUID id, @RequestBody UpdateAcademicDayRequest entity) {
    var updatedDay = academicDayService.updateAcademicDay(id, entity);

    return ResponseEntity.ok(updatedDay);
  }
}
