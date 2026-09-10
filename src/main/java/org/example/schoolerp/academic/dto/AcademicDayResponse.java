package org.example.schoolerp.academic.dto;

import java.time.LocalDate;
import org.example.schoolerp.academic.entity.DayType;

public record AcademicDayResponse(LocalDate date, DayType dayType, String note) {}
