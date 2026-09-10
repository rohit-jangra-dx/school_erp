package org.example.schoolerp.academic.service;

import java.time.LocalDate;
import java.util.List;

public interface HolidayProvider {
  List<Holiday> getHolidays(LocalDate start, LocalDate end);
}
