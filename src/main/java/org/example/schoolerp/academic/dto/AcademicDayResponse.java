package org.example.schoolerp.academic.dto;

import java.time.LocalDate;
import java.util.UUID;
import org.example.schoolerp.academic.entity.AcademicDay;
import org.example.schoolerp.academic.entity.DayType;

public record AcademicDayResponse(UUID id, LocalDate date, DayType dayType, String note) {
  public AcademicDayResponse(AcademicDay day) {
    this(day.getId(), day.getDate(), day.getDayType(), day.getNote());
  }
}
