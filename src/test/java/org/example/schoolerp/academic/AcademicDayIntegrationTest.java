package org.example.schoolerp.academic;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.util.HashMap;
import org.example.schoolerp.fixtures.AcademicFixtures;
import org.example.schoolerp.testsupport.AuthTestSupport;
import org.example.schoolerp.testsupport.DatabaseCleanupExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

@SpringBootTest
@ExtendWith(DatabaseCleanupExtension.class)
@AutoConfigureMockMvc
public class AcademicDayIntegrationTest extends AuthTestSupport {

  @Autowired private AcademicFixtures academicFixtures;

  private LoggedInUser admin;

  @AfterEach
  void setup() throws Exception {
    admin = loginAsNewUser("TestOrg", "TestUser", "TestPass");
  }

  @Test
  void get_academic_day_by_id_successfully() {}

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

  @Test
  void update_days_in_mass_successfully() {}

  @Test
  void update_day_successfully() {}
}
