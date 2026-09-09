package org.example.schoolerp.academic.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class CreateAcademicCalendarRequest {
  @NotNull private LocalDate startDate;
  @NotNull private LocalDate endDate;
}
