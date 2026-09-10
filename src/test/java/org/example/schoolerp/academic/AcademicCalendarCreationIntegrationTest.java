package org.example.schoolerp.academic;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.example.schoolerp.testsupport.AuthTestSupport;
import org.example.schoolerp.testsupport.DatabaseCleanupExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(DatabaseCleanupExtension.class)
public class AcademicCalendarCreationIntegrationTest extends AuthTestSupport {

  private LoggedInUser admin;

  @BeforeEach
  void setup() throws Exception {
    admin = loginAsNewUser("testOrg", "test_user", "test_pass");
  }

  @Test
  void create_academic_calendar_successfully() throws Exception {
    String body =
        """
                {
                    "startDate":"2026-01-01",
                    "endDate":"2027-01-01"
                }
                """;
    mockMvc
        .perform(
            authed(post("/academic-calendars"), admin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.startDate").value("2026-01-01"))
        .andExpect(jsonPath("$.endDate").value("2027-01-01"));
  }

  @Test
  void get_academic_calendar_successfully() throws Exception {
    String body =
        """
                {
                    "startDate":"2026-01-01",
                    "endDate":"2026-12-31"
                }
                """;
    var response =
        mockMvc
            .perform(
                authed(post("/academic-calendars"), admin)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.startDate").value("2026-01-01"))
            .andExpect(jsonPath("$.endDate").value("2026-12-31"))
            .andReturn();

    var calendarId = JsonPath.read(response.getResponse().getContentAsString(), "$.id");

    mockMvc
        .perform(authed(get("/academic-calendars/{id}", calendarId), admin))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(365))
        .andExpect(jsonPath("$.[*].date").isNotEmpty())
        .andExpect(jsonPath("$[0].date").value("2026-01-01"))
        .andExpect(jsonPath("$[364].date").value("2026-12-31"));
  }

  @Test
  void get_academic_calendar_by_range_successfully() throws Exception {
    String body =
        """
                {
                    "startDate":"2026-01-01",
                    "endDate":"2026-12-31"
                }
                """;
    var response =
        mockMvc
            .perform(
                authed(post("/academic-calendars"), admin)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.startDate").value("2026-01-01"))
            .andExpect(jsonPath("$.endDate").value("2026-12-31"))
            .andReturn();

    var calendarId = JsonPath.read(response.getResponse().getContentAsString(), "$.id");

    mockMvc
        .perform(
            authed(
                get("/academic-calendars/{id}/days", calendarId)
                    .param("from", "2026-01-01")
                    .param("to", "2026-01-31"),
                admin))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(31))
        .andExpect(jsonPath("$.[*].date").isNotEmpty())
        .andExpect(jsonPath("$[0].date").value("2026-01-01"))
        .andExpect(jsonPath("$[30].date").value("2026-01-31"));
  }
}
