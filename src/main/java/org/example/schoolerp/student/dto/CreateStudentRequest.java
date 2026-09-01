package org.example.schoolerp.student.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class CreateStudentRequest {
  @Email private String email;

  @NotBlank private String fullName;

  @NotNull private LocalDate dob;

  @NotBlank private String gender;

  @NotBlank private String address;

  @NotNull private Integer currentRollNumber;

  @NotNull private Integer currentClass;
}
