package org.example.schoolerp.student;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.schoolerp.student.dto.CreateGuardianRequest;
import org.example.schoolerp.student.dto.CreateGuardianResponse;
import org.example.schoolerp.student.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/students")
@RequiredArgsConstructor
public class GuradianController {

  private final StudentService studentService;

  @PostMapping("/{studentId}/guardians")
  public ResponseEntity<CreateGuardianResponse> add(
      @PathVariable UUID studentId, @Valid @RequestBody CreateGuardianRequest request) {
    var res = studentService.addGuardian(studentId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(res);
  }

  @DeleteMapping("/{studentId}/guardians/{guardianId}")
  public ResponseEntity<Void> remove(@PathVariable UUID studentId, @PathVariable UUID guardianId) {
    studentService.removeGuardian(studentId, guardianId);

    return ResponseEntity.noContent().build();
  }
}
