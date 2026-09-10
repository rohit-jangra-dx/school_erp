package org.example.schoolerp.academic.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAcademicClassRequest {
  @NotBlank private String name;
}
