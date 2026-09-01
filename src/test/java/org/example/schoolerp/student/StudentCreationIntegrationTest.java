package org.example.schoolerp.student;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.example.schoolerp.student.service.ImportResult;
import org.example.schoolerp.testsupport.AuthTestSupport;
import org.example.schoolerp.testsupport.DatabaseCleanupExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(DatabaseCleanupExtension.class)
public class StudentCreationIntegrationTest extends AuthTestSupport {

  private final ObjectMapper objectMapper = new ObjectMapper();

  private LoggedInUser admin;

  void setup() throws Exception {
    admin = loginAsNewUser("SchoolOrgA", "teacher1", "correct-horse");
  }

  @Test
  void creating_single_student_succeeds() throws Exception {
    setup();
    String body =
        """
                {
                    "fullName":"rohit jangra",
                    "email": "rohit@gmail.com",
                    "dob": "2023-03-03",
                    "gender": "Male",
                    "address": "nukkkad gali",
                    "currentRollNumber": "23",
                    "currentClass": "10"
                }
                """;

    mockMvc
        .perform(
            authed(post("/students"), admin).contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isCreated());
  }

  @Test
  void get_studentsTemplate_file_successfully() throws Exception {
    setup();

    mockMvc
        .perform(authed(get("/students/import/template"), admin))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("text/csv"))
        .andExpect(
            header()
                .string("Content-Disposition", "attachment; filename=\"students-template.csv\""))
        .andExpect(
            content()
                .string(
                    "full_name,email,dob,gender,address,current_roll_number,current_class"
                        + System.lineSeparator()));
  }

  @Test
  void get_studentsTemplate_file_and_import_data_through_file_successfully() throws Exception {
    setup();

    MvcResult result =
        mockMvc
            .perform(authed(get("/students/import/template"), admin))
            .andExpect(status().isOk())
            .andReturn();

    byte[] csvBytes = result.getResponse().getContentAsByteArray();

    String csv = new String(csvBytes, StandardCharsets.UTF_8);

    csv +=
        """

                    John Doe,john@example.com,2005-08-30,Male,Some Address,12,10
                    Bohn Doe,bohn@example.com,2005-08-30,Male,Some Address,12,10
                    """;
    MockMultipartFile file =
        new MockMultipartFile(
            "file", "students.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

    MvcResult result2 =
        mockMvc
            .perform(authed(multipart("/students/import").file(file), admin))
            .andExpect(status().isOk())
            .andReturn();

    ImportResult importResult =
        objectMapper.readValue(result2.getResponse().getContentAsString(), ImportResult.class);

    assertThat(importResult.imported()).isEqualTo(2);
    assertThat(importResult.skipped()).isEqualTo(0);
    assertThat(importResult.errors()).isEmpty();
  }

  @Test
  void get_studentsTemplate_file_and_import_invalid_data_through_file_and_get_correct_response()
      throws Exception {
    setup();

    MvcResult result =
        mockMvc
            .perform(authed(get("/students/import/template"), admin))
            .andExpect(status().isOk())
            .andReturn();

    byte[] csvBytes = result.getResponse().getContentAsByteArray();

    String csv = new String(csvBytes, StandardCharsets.UTF_8);

    csv +=
        """

                    John Doe,john@example.com,2005-08-30,Some Address,12,10
                    Bohn Doe,bohn@example.com,2005-08-30,Male,Some Address,12,10
                    """;
    MockMultipartFile file =
        new MockMultipartFile(
            "file", "students.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

    MvcResult result2 =
        mockMvc
            .perform(authed(multipart("/students/import").file(file), admin))
            .andExpect(status().isOk())
            .andReturn();

    ImportResult importResult =
        objectMapper.readValue(result2.getResponse().getContentAsString(), ImportResult.class);

    assertThat(importResult.imported()).isEqualTo(1);
    assertThat(importResult.skipped()).isEqualTo(1);
    assertThat(importResult.errors()).isNotEmpty();
  }
}
