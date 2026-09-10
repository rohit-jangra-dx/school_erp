package org.example.schoolerp.academic.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.schoolerp.academic.dto.AcademicDayResponse;
import org.example.schoolerp.academic.dto.AcademicYearResponse;
import org.example.schoolerp.academic.entity.AcademicYear;
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
  private final AcademicDayService academicDayService;

  @Transactional
  public AcademicYearResponse createCalendar(LocalDate start, LocalDate end) {
    var year = academicYearRepository.save(new AcademicYear(start, end));
    var academicDays = academicDayService.createAcademicDays(year);

    academicDayRepository.saveAll(academicDays);

    return new AcademicYearResponse(year.getId(), year.getStartDate(), year.getEndDate());
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
