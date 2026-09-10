package org.example.schoolerp.academic.dto;

import java.time.LocalDate;
import java.util.UUID;
import org.example.schoolerp.academic.entity.AcademicYear;

public record AcademicYearResponse(UUID id, LocalDate startDate, LocalDate endDate) {
  public AcademicYearResponse(AcademicYear year) {
    this(year.getId(), year.getStartDate(), year.getEndDate());
  }
}
