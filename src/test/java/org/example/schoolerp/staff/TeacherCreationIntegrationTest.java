package org.example.schoolerp.staff;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.example.schoolerp.testsupport.AuthTestSupport;
import org.example.schoolerp.testsupport.DatabaseCleanupExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ExtendWith(DatabaseCleanupExtension.class)
@AutoConfigureMockMvc
public class TeacherCreationIntegrationTest extends AuthTestSupport {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private LoggedInUser admin;

    @BeforeEach
    void setup() throws Exception {
        admin = loginAsNewUser("test", "test_username", "test_password");
    }

    @Test
    void create_teacher_account_successfully() throws Exception {
        String body = """
                    {
                        "fullName": "test_kumar",
                        "phoneNo": "9306188213",
                        "email": "one@gmail.com",
                        "dob": "2023-12-02",
                        "gender": "Male",
                        "address": "Test Address"
                    }
                    """;

        MvcResult result = mockMvc.perform(authed(post("/staff/teachers"), admin)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
        )
        .andExpect(status().isCreated())
        .andReturn();

        var response = objectMapper.readValue(result.getResponse().getContentAsString(), CreateTeacherResponse.class);
        
        assertThat(response.getUsername()).isEqualTo("one@gmail.com");
        
        // to make sure legit loginable user got created
        var loggedInUser = loginAsExistingUser(admin.organization().getName(), "one@gmail.com", "2023-12-02");
        
        assertThat(loggedInUser).isNotNull();
    }


}
