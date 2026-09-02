package org.example.schoolerp.student.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.schoolerp.identity.service.RegistrationService;
import org.example.schoolerp.student.Guardian;
import org.example.schoolerp.student.GuardianRepository;
import org.example.schoolerp.student.Student;
import org.example.schoolerp.student.StudentRepository;
import org.example.schoolerp.student.dto.CreateGuardianRequest;
import org.example.schoolerp.student.dto.CreateGuardianResponse;
import org.example.schoolerp.student.dto.CreateStudentRequest;
import org.example.schoolerp.student.dto.CreateStudentResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {
  private static final String STUDENT_ROLE = "ROLE_STUDENT";
  private final RegistrationService registerationService;
  private final StudentRepository studentRepository;
  private final GuardianRepository guardianRepository;

  /**
   * createStudent creates the student + user + authAccount records. it sets the user's role to
   * STUDENT. NOTE: username is student's email as it's guranteed to be unique and password is their
   * dob.
   *
   * @param studentRequest
   * @return CreateStudentResponse
   */
  @Transactional
  public CreateStudentResponse create(CreateStudentRequest studentRequest) {
    var username = studentRequest.getEmail();
    var password = studentRequest.getDob().toString();

    var user = registerationService.registerUser(username, password, STUDENT_ROLE);

    var student =
        new Student(
            user,
            studentRequest.getFullName(),
            studentRequest.getEmail(),
            studentRequest.getDob(),
            studentRequest.getGender(),
            studentRequest.getAddress(),
            studentRequest.getCurrentRollNumber(),
            studentRequest.getCurrentClass());

    studentRepository.save(student);

    var res = new CreateStudentResponse();
    res.setId(student.getId());
    res.setUsername(username);

    return res;
  }

  @Transactional
  public CreateGuardianResponse addGuardian(UUID studentId, CreateGuardianRequest request) {
    var student =
        studentRepository
            .findById(studentId)
            .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

    var guardian =
        guardianRepository
            .findByEmailAndRelation(request.getEmail(), request.getRelation())
            .orElseGet(
                () -> {
                  var newGuardian =
                      new Guardian(
                          request.getFullName(),
                          request.getEmail(),
                          request.getPhoneNo(),
                          request.getRelation());

                  return guardianRepository.save(newGuardian);
                });

    student.addGuardian(guardian);

    var res = new CreateGuardianResponse();
    res.setFullName(guardian.getFullName());
    res.setId(guardian.getId());

    return res;
  }

  @Transactional
  public void removeGuardian(UUID studentId, UUID guardianId) {
    var student =
        studentRepository
            .findById(studentId)
            .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

    var guardian =
        guardianRepository
            .findById(guardianId)
            .orElseThrow(() -> new IllegalArgumentException("Guardian not found: " + guardianId));

    student.removeGuardian(guardian);
  }
}
