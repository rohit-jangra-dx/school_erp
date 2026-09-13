package org.example.schoolerp.academic;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.util.HashMap;
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
  void get_academic_days_by_year_id_successfully() throws Exception {
    var request = academicFixtures.createAcademicCalendarRequest();

    var response =
        postJson("/academic-calendars", admin, request).andExpect(status().isOk()).andReturn();

    var calendarId = JsonPath.read(response.getResponse().getContentAsString(), "$.id");

    getJson("/academic-calendars/{id}/days", admin, null, calendarId)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(365))
        .andExpect(jsonPath("$.[*].date").isNotEmpty())
        .andExpect(jsonPath("$[0].date").value(request.getStartDate().toString()))
        .andExpect(jsonPath("$[364].date").value(request.getStartDate().toString()));
  }

  @Test
  void get_academic_days_by_year_id_and_range_successfully() throws Exception {
    var request = academicFixtures.createAcademicCalendarRequest();

    var response =
        postJson("/academic-calendars", admin, request)
            .andExpect(status().isOk())
            .andExpect(status().isCreated())
            .andReturn();

    var calendarId = JsonPath.read(response.getResponse().getContentAsString(), "$.id");

    // 1 month range
    var queryParams = new HashMap<String, String>();
    queryParams.put("from", request.getStartDate().toString());
    queryParams.put("to", request.getStartDate().plusDays(31).toString());

    getJson("/academic-calendars/{id}/days", admin, queryParams, calendarId)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(31))
        .andExpect(jsonPath("$.[*].date").isNotEmpty())
        .andExpect(jsonPath("$[0].date").value("2026-01-01"))
        .andExpect(jsonPath("$[30].date").value("2026-01-31"));
  }
}
