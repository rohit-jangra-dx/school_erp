package org.example.schoolerp.student.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;

@Data
public class CreateGuardianResponse {
  @NotNull private UUID id;
  @NotNull private String fullName;
}
