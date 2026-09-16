package org.example.schoolerp.academic.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateEnrollmentRequest {
  @NotNull private UUID academicYearId;
  @NotNull private UUID classSectionId;
  @NotNull private UUID studentId;
  @NotNull private Integer rollNo;
}
