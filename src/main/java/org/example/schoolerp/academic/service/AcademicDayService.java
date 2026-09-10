package org.example.schoolerp.academic.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.schoolerp.academic.entity.AcademicDay;
import org.example.schoolerp.academic.entity.AcademicYear;
import org.example.schoolerp.academic.entity.DayType;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AcademicDayService {

  private final HolidayProvider holidayProvider;

  public List<AcademicDay> createAcademicDays(AcademicYear academicYear) {
    var start = academicYear.getStartDate();
    var end = academicYear.getEndDate();

    @SuppressWarnings("null")
    var holidays =
        holidayProvider.getHolidays(start, end).stream()
            .collect(Collectors.toMap(Holiday::date, Function.identity()));

    return start
        .datesUntil(end.plusDays(1))
        .map(date -> createAcademicDay(academicYear, date, holidays))
        .toList();
  }

  private AcademicDay createAcademicDay(
      AcademicYear academicYear, LocalDate date, Map<LocalDate, Holiday> holidays) {
    var holiday = holidays.get(date);
    // NOTE: by default it's either holiday or work day
    var dayType =
        holiday == null
            ? DayType.WORKING
            : date.getDayOfWeek().getValue() >= 6 ? DayType.WEEKEND : DayType.WORKING;

    return new AcademicDay(academicYear, date, dayType);
  }
}
