package org.example.schoolerp.academic;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.stream.IntStream;
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
public class EnrollmenQueryIntegrationTest extends AuthTestSupport {

  @Autowired AcademicFixtures academicFixtures;
  private LoggedInUser admin;
  private AcademicEntityPackage aPackage;
  private AcademicEntityPackage aPackage2;

  private record AcademicEntityPackage(
      AcademicYear year, AcademicClass academicClass, ClassSection section) {

    // to stop fro creating overlapping years
    private static int YearCounter = 0;
    private static final LocalDate firstDate = LocalDate.of(2022, 1, 1);

    public static AcademicEntityPackage create(
        LoggedInUser admin, AcademicFixtures academicFixtures) {
      var year = academicFixtures.createAcademicYear(firstDate.plusYears(YearCounter));
      var cls = academicFixtures.createAcademicClass();
      var teacher = academicFixtures.createTeacher(admin.organization());
      var section = academicFixtures.createClassSection(cls, year, teacher, 0);

      YearCounter += 1;
      return new AcademicEntityPackage(year, cls, section);
    }
  }
  ;

  @BeforeEach
  void setup() throws Exception {
    admin = loginAsNewUser("testOrg", "testUser", "testPass");

    aPackage = asTenant(admin.orgId(), () -> AcademicEntityPackage.create(admin, academicFixtures));
    aPackage2 =
        asTenant(admin.orgId(), () -> AcademicEntityPackage.create(admin, academicFixtures));
  }

  @Test
  void get_enrollment_by_id_successfully() throws Exception {
    var student =
        asTenant(
            admin.orgId(),
            () -> {
              return academicFixtures.createStudent(admin.organization());
            });

    var request =
        academicFixtures.createEnrollmentRequest(
            aPackage.year().getId(), aPackage.section().getId(), student.getId(), 0);

    var result =
        postJson("/enrollments", admin, request)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.studentId").value(student.getId().toString()))
            .andReturn();

    var enrollmentId = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

    getJson("/enrollments/{id}", admin, null, enrollmentId)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(enrollmentId));
  }

  @Test
  void get_enrollment_by_class_section_successfully() throws Exception {
    // 0 means A 1 would be section B, also new teacher needed for section
    var otherSection =
        asTenant(
            admin.orgId(),
            () -> {
              var teacher = academicFixtures.createTeacher(admin.organization());
              return academicFixtures.createClassSection(
                  aPackage.academicClass(), aPackage.year(), teacher, 1);
            });

    for (var i = 0; i < 50; i++) {
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

      postJson("/enrollments", admin, request).andExpect(status().isCreated());
    }

    var query = new HashMap<String, String>();
    query.put("classSectionId", otherSection.getId().toString());

    getJson("/enrollments", admin, query)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(25)));
  }

  @Test
  void get_enrollment_by_academic_class_successfully() throws Exception {

    for (var i = 0; i < 50; i++) {
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
                aPackage.year().getId(), aPackage2.section().getId(), student.getId(), i);
      }

      postJson("/enrollments", admin, request).andExpect(status().isCreated());
    }

    var query = new HashMap<String, String>();
    query.put("classSectionId", aPackage2.section().getId().toString());

    getJson("/enrollments", admin, query)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(25)));
  }

  @Test
  void get_enrollment_by_year_successfullY() throws Exception {
    // we have ten distinct years of duration
    var aPackages =
        asTenant(
            admin.orgId(),
            () -> {
              return IntStream.range(0, 10)
                  .mapToObj(i -> AcademicEntityPackage.create(admin, academicFixtures))
                  .toList();
            });

    for (var i = 0; i < 50; i++) {
      var aPackage = aPackages.get(i / 5);
      var student =
          asTenant(admin.orgId(), () -> academicFixtures.createStudent(admin.organization()));

      var request =
          academicFixtures.createEnrollmentRequest(
              aPackage.year().getId(), aPackage.section().getId(), student.getId(), i);

      postJson("/enrollments", admin, request).andExpect(status().isCreated());
    }

    var query = new HashMap<String, String>();
    query.put("academicYearId", aPackages.get(0).year().getId().toString());

    // now this year should have 5 students enrolled
    getJson("/enrollments", admin, query)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(5)));
  }
}
