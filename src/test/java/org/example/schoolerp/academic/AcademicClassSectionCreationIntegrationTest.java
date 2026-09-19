package org.example.schoolerp.academic;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.example.schoolerp.academic.dto.CreateAcademicClassRequest;
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

  @Autowired AcademicFixtures academicFixtures;

  private LoggedInUser admin;
  private Teacher teacher;
  private AcademicYear academicYear;

  private record ClassCreated(Object classId, CreateAcademicClassRequest request) {}
  ;

  private ClassCreated academicClass;

  @BeforeEach
  void setup() throws Exception {
    admin = loginAsNewUser("test_org", "test_user", "test_pass");

    academicYear = asTenant(admin.orgId(), () -> academicFixtures.createAcademicYear());
    teacher = asTenant(admin.orgId(), () -> academicFixtures.createTeacher(admin.organization()));

    // create a class
    var request = academicFixtures.createAcademicClassRequest();

    var result =
        postJson("/academic-classes", admin, request)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.name").value(request.getName()))
            .andExpect(jsonPath("$.sections", hasSize(0)))
            .andReturn();

    var classId = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
    academicClass = new ClassCreated(classId, request);
  }

  @Test
  void create_academic_class_and_its_sections_successfully() throws Exception {
    var classId = academicClass.classId();

    // classSectionBody
    var request2 =
        academicFixtures.createClassSectionRequest(academicYear.getId(), teacher.getId(), 0);

    postJson("/academic-classes/{id}/sections", admin, request2, classId)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value(request2.getName()))
        .andExpect(jsonPath("$.id").isNotEmpty());
  }

  @Test
  void a_teacher_can_only_be_homeroom_teacher_in_single_section() throws Exception {
    var classId = academicClass.classId();

    // First time assigning teacher to a section
    var request2 =
        academicFixtures.createClassSectionRequest(academicYear.getId(), teacher.getId(), 0);

    postJson("/academic-classes/{id}/sections", admin, request2, classId)
        .andExpect(status().isCreated());

    // Again assigning the same teacher to different section (should throw error)
    var request3 =
        academicFixtures.createClassSectionRequest(academicYear.getId(), teacher.getId(), 1);

    postJson("/academic-classes/{id}/sections", admin, request3, classId)
        .andExpect(status().isConflict());
  }

  @Test
  void all_section_names_under_same_class_and_year_are_unique() throws Exception {
    var classId = academicClass.classId();

    // section name is A here
    var request2 =
        academicFixtures.createClassSectionRequest(academicYear.getId(), teacher.getId(), 0);

    postJson("/academic-classes/{id}/sections", admin, request2, classId)
        .andExpect(status().isCreated());

    // Again keeping the name Same A (changing the teacher but keeping the year same)
    teacher = asTenant(admin.orgId(), () -> academicFixtures.createTeacher(admin.organization()));
    var request3 =
        academicFixtures.createClassSectionRequest(academicYear.getId(), teacher.getId(), 0);

    postJson("/academic-classes/{id}/sections", admin, request3, classId)
        .andExpect(status().isConflict());
  }

  @Test
  void update_class_name_successfully() throws Exception {
    var classId = academicClass.classId();

    var updateClassRequest = academicFixtures.updateAcademicClassRequest();
    putJson("/academic-classes/{id}", admin, updateClassRequest, classId)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(updateClassRequest.getName()));
  }

  @Test
  void update_class_sections_successfully() throws Exception {
    var classId = academicClass.classId();

    // first create section
    var request2 =
        academicFixtures.createClassSectionRequest(academicYear.getId(), teacher.getId(), 0);

    var result =
        postJson("/academic-classes/{id}/sections", admin, request2, classId)
            .andExpect(status().isCreated())
            .andReturn();

    var sectionId = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
    assert sectionId != null;

    var updateClassSectionRequest = academicFixtures.updateClassSectionRequest(0);

    putJson("/academic-classes/sections/{id}", admin, updateClassSectionRequest, sectionId)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(updateClassSectionRequest.getName()))
        .andExpect(jsonPath("$.room").value(updateClassSectionRequest.getRoom()))
        .andExpect(jsonPath("$.capacity").value(updateClassSectionRequest.getCapacity()));
  }
}
