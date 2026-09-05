package org.example.schoolerp.staff;

import org.example.schoolerp.identity.service.RegistrationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeacherService {
    private static final String TEACHER_ROLE = "ROLE_TEACHER";

    private final RegistrationService registrationService;
    private final TeacherRepository teacherRepository;

    @Transactional
    public CreateTeacherResponse create(CreateTeacherRequest request) {
        var username = request.getEmail();
        var password = request.getDob().toString();

        var user = registrationService.registerUser(username, password, TEACHER_ROLE);

        log.debug("user created successfully: {}, username: {}", user, user.getUsername());
        log.debug("HELLO");
        var teacher = new Teacher(user, request.getFullName(), request.getPhoneNo(), request.getEmail(), request.getDob(),
                request.getGender(), request.getAddress());

        teacher = teacherRepository.save(teacher);

        var response = new CreateTeacherResponse();
        response.setId(teacher.getId());
        response.setUsername(username);

        return response;
    }

}
