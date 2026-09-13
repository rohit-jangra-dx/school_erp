package org.example.schoolerp.academic.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.schoolerp.academic.entity.DayType;

@Data
@AllArgsConstructor
public class UpdateAcademicDaybyDateRequest {
  @NotNull private LocalDate date;
  private DayType dayType;
  private String note;
}
