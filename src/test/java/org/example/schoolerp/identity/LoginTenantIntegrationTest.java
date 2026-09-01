package org.example.schoolerp.identity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.example.schoolerp.organization.Organization;
import org.example.schoolerp.security.auth.JwtService;
import org.example.schoolerp.security.tenant.TenantContext;
import org.example.schoolerp.testsupport.DatabaseCleanupExtension;
import org.example.schoolerp.testsupport.TenantFixtures;
import org.example.schoolerp.testsupport.TenantTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(DatabaseCleanupExtension.class)
public class LoginTenantIntegrationTest extends TenantTestSupport {

  @Autowired private MockMvc mockMvc;
  @Autowired private TenantFixtures fixtures;
  @Autowired private JwtService jwtService;

  private TenantFixtures.TenantFixture tenantA;
  private TenantFixtures.TenantFixture tenantB;

  @BeforeEach
  void setup() {
    Organization orgA = fixtures.createOrg("LoginOrgA");
    Organization orgB = fixtures.createOrg("LoginOrgB");

    tenantA = asTenant(orgA.getId(), () -> fixtures.createUser(orgA, "alice", "correct-horse"));
    tenantB = asTenant(orgB.getId(), () -> fixtures.createUser(orgB, "bob", "battery-staple"));
  }

  @Test
  void login_withCorrectTenantAndCredentials_returnsTokenScopedToThatTenant() throws Exception {
    String body =
        """
            {"organizationId":"%s","username":"alice","password":"correct-horse"}
            """
            .formatted(tenantA.orgId());

    var result =
        mockMvc
            .perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(body))
            // .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andReturn();

    String token = result.getResponse().getContentAsString();
    UUID claimOrg = jwtService.extractOrganizationId(token);
    assertThat(claimOrg).isEqualTo(tenantA.orgId());

    // this means context don't leak to next request that runs on this thread
    assertThat(TenantContext.get()).isNull();
  }

  @Test
  void login_withWrongOrganizationId_failsAndClearsContext() throws Exception {
    String body =
        """
            {"organizationId":"%s","username":"alice","password":"correct-horse"}
            """
            .formatted(UUID.randomUUID());

    mockMvc
        .perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isUnauthorized());

    assertThat(TenantContext.get()).isNull();
  }

  @Test
  void login_withWrongPassword_failsAndClearsContext() throws Exception {
    String body =
        """
            {"organizationId":"%s","username":"alice","password":"wrong-horse"}
            """
            .formatted(tenantA.orgId());

    mockMvc
        .perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isUnauthorized());

    assertThat(TenantContext.get()).isNull();
  }

  @Test
  void login_userFromOtherTenant_cannotAuthenticateAcrossOrgs() throws Exception {
    String body =
        """
            {"organizationId":"%s","username":"alice","password":"correct-horse"}
            """
            .formatted(tenantB.orgId());

    mockMvc
        .perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isUnauthorized());

    assertThat(TenantContext.get()).isNull();
  }
}
