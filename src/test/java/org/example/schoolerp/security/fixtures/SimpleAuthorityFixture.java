package org.example.schoolerp.security.fixtures;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component
public class SimpleAuthorityFixture {

  @PreAuthorize("hasAuthority('student:read')")
  public void readStudent() {}
}
