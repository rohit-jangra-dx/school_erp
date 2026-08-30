package org.example.schoolerp.student.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.schoolerp.identity.service.RegisterationService;
import org.example.schoolerp.student.Student;
import org.example.schoolerp.student.StudentRepository;
import org.example.schoolerp.student.dto.CreateStudentRequest;
import org.example.schoolerp.student.dto.CreateStudentResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {
  private static final String STUDENT_ROLE = "ROLE_STUDENT";
  private final RegisterationService registerationService;
  private final StudentRepository studentRepository;

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
            studentRequest.getCurrenRollNumber(),
            studentRequest.getCurrentClass());

    studentRepository.save(student);

    var res = new CreateStudentResponse();
    res.setUsername(username);

    return res;
  }
}
