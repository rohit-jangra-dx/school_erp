package org.example.schoolerp.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PermissionScannerTest {
   
    @Autowired
    private PermissionScanner scanner;

    @Test
    void shouldFindDeclaredPermissions() {
        Set<String> permissions = scanner.scan();

        assertThat(permissions)
            .containsExactlyInAnyOrder(
                "student:read",
                "student:create",
                "student:update",
                "student:delete"
            );
    }
}
