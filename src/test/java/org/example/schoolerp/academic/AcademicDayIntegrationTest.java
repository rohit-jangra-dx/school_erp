package org.example.schoolerp.academic;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.example.schoolerp.academic.dto.CreateAcademicCalendarRequest;
import org.example.schoolerp.academic.dto.UpdateAcademicDaybyDateRequest;
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
@Slf4j
public class AcademicDayIntegrationTest extends AuthTestSupport {

  @Autowired private AcademicFixtures academicFixtures;

  private LoggedInUser admin;

  private record AcademicYearCreated(CreateAcademicCalendarRequest request, Object calendarId) {}
  ;

  private AcademicYearCreated academicYearCreated;

  @BeforeEach
  void setup() throws Exception {
    admin = loginAsNewUser("TestOrg", "TestUser", "TestPass");

    // create a new academic year
    var request = academicFixtures.createAcademicCalendarRequest();
    var response =
        postJson("/academic-calendars", admin, request).andExpect(status().isCreated()).andReturn();
    var calendarId = JsonPath.read(response.getResponse().getContentAsString(), "$.id");

    academicYearCreated = new AcademicYearCreated(request, calendarId);
  }

  @Test
  void get_academic_day_by_id_successfully() throws Exception {
    var calendarId = academicYearCreated.calendarId();

    var result =
        getJson("/academic-calendars/{id}/days", admin, null, calendarId)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").isNotEmpty())
            .andReturn();

    var dayId = JsonPath.read(result.getResponse().getContentAsString(), "$[0].id");

    getJson("/academic-calendars/days/{id}", admin, null, dayId)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(dayId));
  }

  @Test
  void get_academic_days_by_year_id_successfully() throws Exception {
    var request = academicYearCreated.request();
    var calendarId = academicYearCreated.calendarId();

    getJson("/academic-calendars/{id}/days", admin, null, calendarId)
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.length()")
                .value(ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1))
        .andExpect(jsonPath("$.[*].date").isNotEmpty())
        .andExpect(jsonPath("$[0].date").value(request.getStartDate().toString()))
        .andExpect(jsonPath("$[364].date").value(request.getEndDate().toString()));
  }

  @Test
  void get_academic_days_by_year_id_and_range_successfully() throws Exception {
    var request = academicYearCreated.request();
    var calendarId = academicYearCreated.calendarId();

    // 1 month range
    var queryParams = new HashMap<String, String>();
    queryParams.put("from", request.getStartDate().toString());
    queryParams.put("to", request.getStartDate().plusDays(30).toString());

    getJson("/academic-calendars/{id}/days", admin, queryParams, calendarId)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(31))
        .andExpect(jsonPath("$.[*].date").isNotEmpty())
        .andExpect(jsonPath("$[0].date").value(request.getStartDate().toString()))
        .andExpect(jsonPath("$[30].date").value(request.getStartDate().plusDays(30).toString()));
  }

  @Test
  void update_days_in_mass_successfully() throws Exception {
    // url is /academic-calendars/{id}/days
    var aReq = academicYearCreated.request();
    var calendarId = academicYearCreated.calendarId();

    var requests = new ArrayList<UpdateAcademicDaybyDateRequest>(10);
    for (var i = 0; i < 10; i++) {
      requests.add(
          academicFixtures.updateAcademicDaybyDateRequest(aReq.getStartDate(), aReq.getEndDate()));
    }

    putJson("/academic-calendars/{id}/days", admin, requests, calendarId)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(10)));
  }

  @Test
  void update_day_successfully() throws Exception {
    var calendarId = academicYearCreated.calendarId();

    var daysResponse =
        getJson("/academic-calendars/{id}/days", admin, null, calendarId)
            .andExpect(status().isOk())
            .andReturn();

    var dayId = JsonPath.read(daysResponse.getResponse().getContentAsString(), "$[0].id");

    var request = academicFixtures.updateAcademicDayRequest();

    putJson("/academic-calendars/days/{id}", admin, request, dayId)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(dayId))
        .andExpect(jsonPath("$.date").isNotEmpty())
        .andExpect(jsonPath("$.dayType").value(request.getDayType().toString()))
        .andExpect(jsonPath("$.note").value(request.getNote()));
  }
}
