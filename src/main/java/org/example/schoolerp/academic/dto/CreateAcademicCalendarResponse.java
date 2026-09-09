package org.example.schoolerp.academic.dto;

import java.time.LocalDate;
import java.util.UUID;

public record CreateAcademicCalendarResponse(UUID id, LocalDate startDate, LocalDate endDate) {}
