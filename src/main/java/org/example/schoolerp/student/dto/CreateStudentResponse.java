package org.example.schoolerp.student.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.Data;

@Data
public class CreateStudentResponse {
  @NotBlank private UUID id;
  @NotBlank private String username;
}
