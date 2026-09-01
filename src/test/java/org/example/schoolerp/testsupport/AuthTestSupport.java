package org.example.schoolerp.testsupport;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.example.schoolerp.organization.Organization;
import org.example.schoolerp.security.auth.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

public class AuthTestSupport extends TenantTestSupport {

  @Autowired protected MockMvc mockMvc;
  @Autowired protected TenantFixtures fixtures;
  @Autowired protected JwtService jwtService;

  public record LoggedInUser(
      UUID orgId, Organization organization, String username, String token) {}
  ;

  /**
   * loginAsNewUser creates an org + a loginable user, logs them in via the real /login endpoint,
   * and returns their JWT. This is real authentication process not fabricated token is created
   * here.
   *
   * @param orgName
   * @param username
   * @param rawPassoword
   * @return LoggedInUser
   * @throws Exception
   */
  protected LoggedInUser loginAsNewUser(String orgName, String username, String rawPassoword)
      throws Exception {
    Organization org = fixtures.createOrg(orgName);
    asTenant(org.getId(), () -> fixtures.createUser(org, username, rawPassoword));

    String body =
        """
                {"organizationId": "%s","username":"%s","password":"%s"}
                """
            .formatted(org.getId(), username, rawPassoword);

    var result =
        mockMvc
            .perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andReturn();

    String token = result.getResponse().getContentAsString();
    return new LoggedInUser(org.getId(), org, username, token);
  }

  /* Attaches the Bearer token to any MockMvc request builder */
  protected MockHttpServletRequestBuilder authed(
      MockHttpServletRequestBuilder builder, LoggedInUser user) {
    return builder.header("Authorization", "Bearer " + user.token());
  }

  protected MockMultipartHttpServletRequestBuilder authed(
      MockMultipartHttpServletRequestBuilder builder, LoggedInUser user) {
    return builder.header("Authorization", "Bearer " + user.token);
  }
}
