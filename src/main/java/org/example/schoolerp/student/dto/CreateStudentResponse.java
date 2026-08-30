package org.example.schoolerp.student.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateStudentResponse {
  @NotBlank private String username;
}
