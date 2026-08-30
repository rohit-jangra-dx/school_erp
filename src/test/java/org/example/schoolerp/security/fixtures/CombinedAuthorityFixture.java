package org.example.schoolerp.security.fixtures;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component
public class CombinedAuthorityFixture {

  @PreAuthorize(
      """
        hasAuthority('student:read') ||
        hasAnyAuthority(
            'student:update',
            'student:delete'
        )
        """)
  public void manageStudent() {}
}
