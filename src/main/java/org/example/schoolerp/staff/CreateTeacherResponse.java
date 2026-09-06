package org.example.schoolerp.staff;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.Data;

@Data
public class CreateTeacherResponse {
  @NotBlank private UUID id;

  @NotBlank private String username;
}
