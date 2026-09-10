package org.example.schoolerp.academic;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.example.schoolerp.academic.dto.CreateAcademicClassRequest;
import org.example.schoolerp.academic.dto.CreateClassSectionRequest;
import org.example.schoolerp.academic.entity.AcademicYear;
import org.example.schoolerp.fixtures.AcademicFixtures;
import org.example.schoolerp.staff.Teacher;
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
@ExtendWith(DatabaseCleanupExtension.class)
@AutoConfigureMockMvc
public class AcademicClassSectionCreationIntegrationTest extends AuthTestSupport {

  @Autowired AcademicFixtures fixtures;
  private final ObjectMapper objectMapper = new ObjectMapper();

  private LoggedInUser admin;
  private Teacher teacher;
  private AcademicYear academicYear;

  @BeforeEach
  void setup() throws Exception {
    admin = loginAsNewUser("test_org", "test_user", "test_pass");

    academicYear = asTenant(admin.orgId(), () -> fixtures.createYear());
    teacher = asTenant(admin.orgId(), () -> fixtures.createTeacher(admin.organization()));
  }

  @Test
  void create_academic_class_and_its_sections_successfully() throws Exception {
    var request = new CreateAcademicClassRequest("1");

    var result =
        mockMvc
            .perform(
                authed(post("/academic-classes"), admin)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.name").value("1"))
            .andReturn();

    var classId = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

    // classSectionBody
    var request2 =
        new CreateClassSectionRequest(academicYear.getId(), teacher.getId(), "A", 10, 30);

    mockMvc
        .perform(
            authed(post("/academic-classes/{id}/sections", classId), admin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("A"))
        .andExpect(jsonPath("$.id").isNotEmpty());
  }
}
