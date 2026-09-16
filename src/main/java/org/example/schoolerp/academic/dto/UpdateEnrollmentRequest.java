package org.example.schoolerp.academic.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateEnrollmentRequest {
  private UUID classSectionId;
}
