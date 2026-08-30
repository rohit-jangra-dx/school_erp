package org.example.schoolerp.security.fixtures;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component
public class NonAuthorityFixture {

  @PreAuthorize("hasRole('ADMIN')")
  public void adminOnly() {}

  @PreAuthorize("@securityService.canAccess()")
  public void customAuthorization() {}
}
