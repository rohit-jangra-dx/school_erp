package org.example.schoolerp.academic.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

  @PostMapping("")
  public String createEnrollment(@RequestBody String entity) {
    // TODO: process POST request

    return entity;
  }

  @GetMapping("")
  public String getAllEnrollments(@RequestParam String param) {
    return new String();
  }

  @GetMapping("/{yearId}")
  public String getEnrollmentsByYear(@PathVariable UUID yearId) {
    return new String();
  }
}
