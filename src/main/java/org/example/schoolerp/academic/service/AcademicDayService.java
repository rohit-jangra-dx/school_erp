package org.example.schoolerp.academic.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.schoolerp.academic.dto.AcademicDayResponse;
import org.example.schoolerp.academic.dto.UpdateAcademicDayRequest;
import org.example.schoolerp.academic.entity.AcademicDay;
import org.example.schoolerp.academic.entity.AcademicYear;
import org.example.schoolerp.academic.entity.DayType;
import org.example.schoolerp.academic.repo.AcademicDayRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AcademicDayService {
  private final AcademicDayRepository academicDayRepository;
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

  @Transactional(readOnly = true)
  public AcademicDayResponse getAcademicDay(UUID id) {
    var day =
        academicDayRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Academic day not found: " + id));
    return new AcademicDayResponse(day);
  }

  @Transactional(readOnly = true)
  public List<AcademicDayResponse> getAcademicDays(UUID id, LocalDate from, LocalDate to) {
    return academicDayRepository.findByAcademicYearIdAndDateBetween(id, from, to).stream()
        .map(AcademicDayResponse::new)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<AcademicDayResponse> getAcademicDays(UUID id) {
    return academicDayRepository.findByAcademicYearId(id).stream()
        .map(AcademicDayResponse::new)
        .toList();
  }

  @Transactional
  public List<AcademicDayResponse> updateAcademicDays(
      UUID academicYearId, List<UpdateAcademicDayRequest> requests) {
    return requests.stream()
        .map(
            req -> {
              var day =
                  academicDayRepository
                      .findByAcademicYearIdAndDate(academicYearId, req.getDate())
                      .orElseThrow(
                          () -> new IllegalArgumentException("Day not found: " + req.getDate()));

              if (req.getNote() != null) {
                day.setNote(req.getNote());
              }
              if (req.getDayType() != null) {
                day.setDayType(req.getDayType());
              }

              return new AcademicDayResponse(day);
            })
        .toList();
  }

  @Transactional
  public AcademicDayResponse updateAcademicDay(UUID id, UpdateAcademicDayRequest request) {
    var day =
        academicDayRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Day not found: " + request.getDate()));

    if (request.getNote() != null) {
      day.setNote(request.getNote());
    }
    if (request.getDayType() != null) {
      day.setDayType(request.getDayType());
    }

    return new AcademicDayResponse(day);
  }
}
