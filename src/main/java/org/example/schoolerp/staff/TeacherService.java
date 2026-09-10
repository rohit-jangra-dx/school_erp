package org.example.schoolerp.staff;

import lombok.RequiredArgsConstructor;
import org.example.schoolerp.identity.service.RegistrationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeacherService {
  private static final String TEACHER_ROLE = "ROLE_TEACHER";

  private final RegistrationService registrationService;
  private final TeacherRepository teacherRepository;

  @Transactional
  public TeacherResponse create(CreateTeacherRequest request) {
    var username = request.getEmail();
    var password = request.getDob().toString();

    var user = registrationService.registerUser(username, password, TEACHER_ROLE);

    var teacher =
        new Teacher(
            user,
            request.getFullName(),
            request.getPhoneNo(),
            request.getEmail(),
            request.getDob(),
            request.getGender(),
            request.getAddress());

    teacher = teacherRepository.save(teacher);

    var response = new TeacherResponse(teacher.getId(), teacher.getUser().getUsername());

    return response;
  }
}
