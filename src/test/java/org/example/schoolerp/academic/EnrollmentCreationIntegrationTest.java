package org.example.schoolerp.academic;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.util.ArrayList;
import org.example.schoolerp.academic.dto.CreateEnrollmentRequest;
import org.example.schoolerp.academic.entity.AcademicClass;
import org.example.schoolerp.academic.entity.AcademicYear;
import org.example.schoolerp.academic.entity.ClassSection;
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
public class EnrollmentCreationIntegrationTest extends AuthTestSupport {

  @Autowired AcademicFixtures academicFixtures;
  private LoggedInUser admin;
  private AcademicEntityPackage aPackage;

  private record AcademicEntityPackage(
      AcademicYear year, AcademicClass academicClass, ClassSection section) {}
  ;

  @BeforeEach
  void setup() throws Exception {
    admin = loginAsNewUser("testOrg", "testUser", "testPass");
    aPackage =
        asTenant(
            admin.orgId(),
            () -> {
              var year = academicFixtures.createAcademicYear();
              var cls = academicFixtures.createAcademicClass();
              var teacher = academicFixtures.createTeacher(admin.organization());
              var section = academicFixtures.createClassSection(cls, year, teacher, 0);

              return new AcademicEntityPackage(year, cls, section);
            });
  }

  @Test
  void create_enrollment_successfully() throws Exception {
    var student =
        asTenant(
            admin.orgId(),
            () -> {
              return academicFixtures.createStudent(admin.organization());
            });

    var request =
        academicFixtures.createEnrollmentRequest(
            aPackage.year().getId(), aPackage.section().getId(), student.getId(), 0);

    postJson("/enrollments", admin, request)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.studentId").value(student.getId().toString()));
  }

  @Test
  void update_enrollment_successfully() throws Exception {
    // first create a section in same class , two sections A and B (other one)
    var otherTeacher =
        asTenant(admin.orgId(), () -> academicFixtures.createTeacher(admin.organization()));
    var otherSection =
        asTenant(
            admin.orgId(),
            () ->
                academicFixtures.createClassSection(
                    aPackage.academicClass(), aPackage.year(), otherTeacher, 1));

    var enrollmentIds = new ArrayList<String>();

    // then add student to both sections
    for (var i = 0; i < 2; i++) {
      var student =
          asTenant(admin.orgId(), () -> academicFixtures.createStudent(admin.organization()));

      CreateEnrollmentRequest request;

      if (i % 2 == 0) {
        request =
            academicFixtures.createEnrollmentRequest(
                aPackage.year().getId(), aPackage.section().getId(), student.getId(), i);
      } else {
        request =
            academicFixtures.createEnrollmentRequest(
                aPackage.year().getId(), otherSection.getId(), student.getId(), i);
      }

      var result =
          postJson("/enrollments", admin, request).andExpect(status().isCreated()).andReturn();
      enrollmentIds.add(JsonPath.read(result.getResponse().getContentAsString(), "$.id"));
    }

    // then switch the section via request at enrollmentIds 0 index we have aPackage sections
    // switch it to other section and check the roll number

    getJson("/enrollments/{id}", admin, null, enrollmentIds.get(0))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.classSection.id").value(aPackage.section().getId().toString()));

    var request2 = academicFixtures.updateEnrollmentRequest(otherSection.getId());
    putJson("/enrollments/{id}", admin, request2, enrollmentIds.get(0))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.classSection.id").value(otherSection.getId().toString()));
  }
}
