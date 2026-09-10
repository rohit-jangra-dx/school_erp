package org.example.schoolerp.academic.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GoogleCalendarService implements HolidayProvider {

  @Override
  public List<Holiday> getHolidays(LocalDate start, LocalDate end) {
    return new ArrayList<Holiday>();
  }
}
