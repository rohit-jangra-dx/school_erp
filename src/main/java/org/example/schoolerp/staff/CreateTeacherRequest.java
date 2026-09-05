package org.example.schoolerp.staff;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTeacherRequest {
    @NotBlank
    private String fullName;
    
    @NotBlank
    private String phoneNo;

    @Email
    @NotBlank
    private String email;
    
    @NotNull
    private LocalDate dob;

    @NotBlank
    private String gender;

    @NotBlank
    private String address;
}