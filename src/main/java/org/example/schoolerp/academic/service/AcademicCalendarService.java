package org.example.schoolerp.academic.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  @Transactional(readOnly = true)
  public List<AcademicYearResponse> getAllAcademicYears() {
    return academicYearRepository.findAll().stream()
        .map(year -> new AcademicYearResponse(year))
        .toList();
  }

  @Transactional(readOnly = true)
  public AcademicYearResponse getAcademicYear(UUID id) {
    var year =
        academicYearRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Year not found: " + id));

    return new AcademicYearResponse(year);
  }
}
