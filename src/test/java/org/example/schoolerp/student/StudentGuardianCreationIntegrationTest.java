package org.example.schoolerp.student;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.example.schoolerp.student.dto.CreateGuardianResponse;
import org.example.schoolerp.student.dto.CreateStudentRequest;
import org.example.schoolerp.student.dto.CreateStudentResponse;
import org.example.schoolerp.student.service.StudentService;
import org.example.schoolerp.testsupport.AuthTestSupport;
import org.example.schoolerp.testsupport.DatabaseCleanupExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(DatabaseCleanupExtension.class)
public class StudentGuardianCreationIntegrationTest extends AuthTestSupport {

  @Autowired private StudentService studentService;

  private ObjectMapper objectMapper = new ObjectMapper();
  private LoggedInUser admin;
  private CreateStudentResponse student;

  @BeforeEach
  void setup() throws Exception {
    admin = loginAsNewUser("anything", "user", "user_password");
    var req = new CreateStudentRequest();
    req.setFullName("anything");
    req.setEmail("anything@gmail.com");
    req.setDob(LocalDate.now());
    req.setGender("male");
    req.setAddress("anything address");
    req.setCurrentRollNumber(20);
    req.setCurrentClass(10);

    student = asTenant(admin.orgId(), () -> studentService.create(req));
  }

  @Test
  void add_guardian_to_valid_student_via_student_id_successfully() throws Exception {
    String body =
        """
                {
                    "fullName":"Papa",
                    "email":"papa@gmail.com",
                    "phoneNo": "2324224424",
                    "relation": "Papa"
                }
                """;

    mockMvc
        .perform(
            authed(post("/students/{studentId}/guardians", student.getId()), admin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isCreated());
  }

  @Test
  void remove_guardian_to_valid_student_via_student_id_successfully() throws Exception {
    String body =
        """
                {
                    "fullName":"Papa",
                    "email":"papa@email.com",
                    "phoneNo": "2324224424",
                    "relation": "Papa"
                }
                """;

    var res =
        mockMvc
            .perform(
                authed(post("/students/{studentId}/guardians", student.getId()), admin)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
            .andExpect(status().isCreated())
            .andReturn();

    CreateGuardianResponse createGuardianResponse =
        objectMapper.readValue(
            res.getResponse().getContentAsString(), CreateGuardianResponse.class);

    var guardian_id = createGuardianResponse.getId();

    mockMvc
        .perform(
            authed(
                delete(
                    "/students/{studentId}/guardians/{guardianId}", student.getId(), guardian_id),
                admin))
        .andExpect(status().isNoContent());
  }
}
