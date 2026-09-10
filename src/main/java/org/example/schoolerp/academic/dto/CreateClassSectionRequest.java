package org.example.schoolerp.academic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateClassSectionRequest {
  @NotNull private UUID academicYearId;
  @NotNull private UUID teacherId;
  @NotBlank private String name;
  @NotNull private Integer room;
  @NotNull private Integer capacity;
}
