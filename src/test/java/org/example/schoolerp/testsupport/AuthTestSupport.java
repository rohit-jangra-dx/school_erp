package org.example.schoolerp.testsupport;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import org.example.schoolerp.fixtures.TenantFixtures;
import org.example.schoolerp.identity.repo.UserRepository;
import org.example.schoolerp.organization.Organization;
import org.example.schoolerp.security.auth.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

public class AuthTestSupport extends TenantTestSupport {
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired protected MockMvc mockMvc;
  @Autowired protected TenantFixtures fixtures;
  @Autowired protected JwtService jwtService;

  @Autowired protected UserRepository userRepository;

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

  protected LoggedInUser loginAsExistingUser(Organization org, String username, String password)
      throws Exception {

    String body =
        """
        {"organizationId": "%s","username":"%s","password":"%s"}
        """
            .formatted(org.getId(), username, password);

    asTenantVoid(
        org.getId(),
        () -> {
          userRepository
              .findByUsername(username)
              .ifPresent(user -> System.out.println("DEBUG:>" + user.getUsername()));
        });

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

  protected ResultActions postJson(
      String url, LoggedInUser user, Object body, Object... pathVariables) throws Exception {
    return mockMvc.perform(
        authed(post(url, pathVariables), user)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)));
  }

  protected ResultActions getJson(
      String url, LoggedInUser user, Map<String, String> params, Object... pathVariables)
      throws Exception {
    var req = authed(get(url, pathVariables), user);

    if (params != null) {
      params.forEach(req::param);
    }
    return mockMvc.perform(req);
  }
}
