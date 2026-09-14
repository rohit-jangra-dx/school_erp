package org.example.schoolerp.academic.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateClassSectionRequest {
  private String name;
  private Integer room;
  private Integer capacity;
  private UUID teacherId;
}
