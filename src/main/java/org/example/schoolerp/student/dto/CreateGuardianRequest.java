package org.example.schoolerp.student.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateGuardianRequest {
  @NotNull private String fullName;
  @NotNull @Email private String email;
  @NotNull private String phoneNo;
  @NotNull private String relation;
}
