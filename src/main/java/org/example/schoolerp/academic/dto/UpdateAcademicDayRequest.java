package org.example.schoolerp.academic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.schoolerp.academic.entity.DayType;

@Data
@AllArgsConstructor
public class UpdateAcademicDayRequest {
  private DayType dayType;
  private String note;
}
