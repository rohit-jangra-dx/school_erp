package org.example.schoolerp.security.tenant;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class TenantIdentifierResolverTest {

  private final TenantIdentifierResolver resolver = new TenantIdentifierResolver();

  @AfterEach
  void cleanup() {
    TenantContext.clear();
  }

  @Test
  void shouldReturnCurrentTenant() {
    UUID tenantId = UUID.randomUUID();
    TenantContext.set(tenantId);

    assertThat(resolver.resolveCurrentTenantIdentifier()).isEqualTo(tenantId);
  }

  @Test
  void shouldReturnNoTenantWhenContextIsEmpty() {
    TenantContext.clear();

    assertThat(resolver.resolveCurrentTenantIdentifier())
        .isEqualTo(TenantIdentifierResolver.NO_TENANT);
  }

  @Test
  void shouldValidateExistingSession() {
    assertThat(resolver.validateExistingCurrentSessions()).isTrue();
  }
}
