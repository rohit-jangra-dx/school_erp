package org.example.schoolerp.academic.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.schoolerp.academic.dto.AcademicDayResponse;
import org.example.schoolerp.academic.dto.CreateAcademicCalendarResponse;
import org.example.schoolerp.academic.entity.AcademicDay;
import org.example.schoolerp.academic.entity.AcademicYear;
import org.example.schoolerp.academic.entity.DayType;
import org.example.schoolerp.academic.repo.AcademicDayRepository;
import org.example.schoolerp.academic.repo.AcademicYearRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AcademicCalendarService {

  private final AcademicYearRepository academicYearRepository;
  private final AcademicDayRepository academicDayRepository;
  private final HolidayProvider holidayProvider;

  @Transactional
  public CreateAcademicCalendarResponse createCalendar(LocalDate start, LocalDate end) {
    var year = academicYearRepository.save(new AcademicYear(start, end));
    var academicDays = createAcademicDays(year);

    academicDayRepository.saveAll(academicDays);

    return new CreateAcademicCalendarResponse(year.getId(), year.getStartDate(), year.getEndDate());
  }

  private List<AcademicDay> createAcademicDays(AcademicYear academicYear) {
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

  public List<AcademicDayResponse> getCalendarDaysById(UUID id) {
    return academicDayRepository.findByAcademicYearId(id).stream()
        .map(day -> new AcademicDayResponse(day.getDate(), day.getDayType(), day.getNote()))
        .toList();
  }

  public List<AcademicDayResponse> getCalendarByDaysByDateRange(
      UUID id, LocalDate from, LocalDate to) {
    return academicDayRepository.findByAcademicYearIdAndDateBetween(id, from, to).stream()
        .map(day -> new AcademicDayResponse(day.getDate(), day.getDayType(), day.getNote()))
        .toList();
  }
}
