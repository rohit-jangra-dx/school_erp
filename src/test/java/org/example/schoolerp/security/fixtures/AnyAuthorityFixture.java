package org.example.schoolerp.security.fixtures;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component
public class AnyAuthorityFixture {

    @PreAuthorize("""
        hasAnyAuthority(
            'student:create',
            'student:update',
            'student:delete'
        )
        """)
    public void modifyStudent() {
    }
}