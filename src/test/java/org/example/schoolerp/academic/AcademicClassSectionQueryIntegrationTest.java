package org.example.schoolerp.academic;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.util.ArrayList;
import org.example.schoolerp.academic.entity.AcademicYear;
import org.example.schoolerp.fixtures.AcademicFixtures;
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
public class AcademicClassSectionQueryIntegrationTest extends AuthTestSupport {

  @Autowired AcademicFixtures fixtures;

  private LoggedInUser admin;
  private AcademicYear academicYear;

  @BeforeEach
  void setup() throws Exception {
    admin = loginAsNewUser("test_org", "test_user", "test_pass");

    academicYear = asTenant(admin.orgId(), () -> fixtures.createYear());
  }

  @Test
  void get_classes_and_their_sections_successfully() throws Exception {
    // get all classIds
    var classes = new ArrayList<Object>(10);

    for (var i = 0; i < 10; i++) {
      var request = fixtures.createAcademicClassRequest();
      var result =
          postJson("/academic-classes", admin, request).andExpect(status().isCreated()).andReturn();

      classes.add(JsonPath.read(result.getResponse().getContentAsString(), "$.id"));
    }

    // create sections in each of them 1 for each
    for (int i = 0; i < 10; i++) {
      var teacher = asTenant(admin.orgId(), () -> fixtures.createTeacher(admin.organization()));
      var sectionRequest =
          fixtures.createClassSectionRequest(academicYear.getId(), teacher.getId(), i);

      postJson("/academic-classes/{id}/sections", admin, sectionRequest, classes.get(i))
          .andExpect(status().isCreated());
    }

    // now fetch their results
    getJson("/academic-classes", admin, null)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(10))) // 10 classes
        .andExpect(jsonPath("$[*].sections[*]", hasSize(10))); // each class has 1 section each
  }

  @Test
  void get_sections_data_by_classId_successfully() throws Exception {
    // get a class
    var request = fixtures.createAcademicClassRequest();

    var result =
        postJson("/academic-classes", admin, request).andExpect(status().isCreated()).andReturn();

    var classId = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

    // create sections on this class
    for (int i = 0; i < 10; i++) {
      var teacher = asTenant(admin.orgId(), () -> fixtures.createTeacher(admin.organization()));
      var sectionRequest =
          fixtures.createClassSectionRequest(academicYear.getId(), teacher.getId(), i);

      postJson("/academic-classes/{id}/sections", admin, sectionRequest, classId)
          .andExpect(status().isCreated());
    }

    // now fetch all the sections of the class
    getJson("/academic-classes/{id}", admin, null, classId)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(10)));
  }
}
