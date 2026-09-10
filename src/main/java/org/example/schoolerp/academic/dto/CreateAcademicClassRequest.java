package org.example.schoolerp.academic.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateAcademicClassRequest {
  @NotBlank private String name;
}
