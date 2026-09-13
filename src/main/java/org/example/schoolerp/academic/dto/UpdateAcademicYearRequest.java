package org.example.schoolerp.academic.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class UpdateAcademicYearRequest {
  private LocalDate startDate;
  private LocalDate endDate;
}
