package org.example.schoolerp.academic;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
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
@AutoConfigureMockMvc
@ExtendWith(DatabaseCleanupExtension.class)
public class AcademicCalendarIntegrationTest extends AuthTestSupport {

  private LoggedInUser admin;
  @Autowired private AcademicFixtures academicFixtures;

  @BeforeEach
  void setup() throws Exception {
    admin = loginAsNewUser("testOrg", "test_user", "test_pass");
  }

  @Test
  void create_academic_calendar_successfully() throws Exception {
    var request = academicFixtures.createAcademicCalendarRequest();

    postJson("/academic-calendars", admin, request)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.startDate").value(request.getStartDate().toString()))
        .andExpect(jsonPath("$.endDate").value(request.getEndDate().toString()));
  }

  @Test
  void creating_overlapping_years_will_fail() throws Exception {
    var request = academicFixtures.createAcademicCalendarRequest();

    postJson("/academic-calendars", admin, request)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.startDate").value(request.getStartDate().toString()))
        .andExpect(jsonPath("$.endDate").value(request.getEndDate().toString()));

    request.setStartDate(request.getStartDate().plusMonths(1));
    request.setEndDate(request.getEndDate().plusMonths(1).minusDays(1));

    // this time it should not be 201
    postJson("/academic-calendars", admin, request).andExpect(status().isConflict());
  }

  @Test
  void get_all_academic_calendars_successfully() throws Exception {
    for (var i = 0; i < 10; i++) {
      var request = academicFixtures.createAcademicCalendarRequest(i);

      postJson("/academic-calendars", admin, request)
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.startDate").value(request.getStartDate().toString()))
          .andExpect(jsonPath("$.endDate").value(request.getEndDate().toString()));
    }

    getJson("/academic-calendars", admin, null)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(10)));
  }

  @Test
  void get_single_academic_year_by_id_successfully() throws Exception {
    var request = academicFixtures.createAcademicCalendarRequest();

    var result =
        postJson("/academic-calendars", admin, request)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.startDate").value(request.getStartDate().toString()))
            .andExpect(jsonPath("$.endDate").value(request.getEndDate().toString()))
            .andReturn();

    var yearId = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

    getJson("/academic-calendars/{id}", admin, null, yearId)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(yearId.toString()));
  }
}
