package org.example.schoolerp.staff;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTeacherResponse {
   @NotBlank
   private UUID id;

   @NotBlank
   private String username; 
}
