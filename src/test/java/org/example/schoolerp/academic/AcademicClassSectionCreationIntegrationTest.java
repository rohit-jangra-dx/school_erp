package org.example.schoolerp.academic;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
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

@SpringBootTest
@ExtendWith(DatabaseCleanupExtension.class)
@AutoConfigureMockMvc
public class AcademicClassSectionCreationIntegrationTest extends AuthTestSupport {

  @Autowired AcademicFixtures fixtures;

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
    var request = fixtures.createAcademicClassRequest();

    var result =
        postJson("/academic-classes", admin, request)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.name").value(request.getName()))
            .andReturn();

    var classId = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

    // classSectionBody
    var request2 = fixtures.createClassSectionRequest(academicYear.getId(), teacher.getId(), 0);

    postJson("/academic-classes/{id}/sections", admin, request2, classId)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value(request2.getName()))
        .andExpect(jsonPath("$.id").isNotEmpty());
  }

  @Test
  void a_teacher_can_only_be_homeroom_teacher_in_single_section() throws Exception {
    var request = fixtures.createAcademicClassRequest();

    var result =
        postJson("/academic-classes", admin, request).andExpect(status().isCreated()).andReturn();

    var classId = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

    // First time assigning teacher to a section
    var request2 = fixtures.createClassSectionRequest(academicYear.getId(), teacher.getId(), 0);

    postJson("/academic-classes/{id}/sections", admin, request2, classId)
        .andExpect(status().isCreated());

    // Again assigning the same teacher to different section (should throw error)
    var request3 = fixtures.createClassSectionRequest(academicYear.getId(), teacher.getId(), 1);

    postJson("/academic-classes/{id}/sections", admin, request3, classId)
        .andExpect(status().isConflict());
  }

  @Test
  void all_section_names_under_same_class_and_year_are_unique() throws Exception {
    var request = fixtures.createAcademicClassRequest();

    var result =
        postJson("/academic-classes", admin, request).andExpect(status().isCreated()).andReturn();

    var classId = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

    // section name is A here
    var request2 = fixtures.createClassSectionRequest(academicYear.getId(), teacher.getId(), 0);

    postJson("/academic-classes/{id}/sections", admin, request2, classId)
        .andExpect(status().isCreated());

    // Again keeping the name Same A (changing the teacher but keeping the year same)
    teacher = asTenant(admin.orgId(), () -> fixtures.createTeacher(admin.organization()));
    var request3 = fixtures.createClassSectionRequest(academicYear.getId(), teacher.getId(), 0);

    postJson("/academic-classes/{id}/sections", admin, request3, classId)
        .andExpect(status().isConflict());
  }
}
