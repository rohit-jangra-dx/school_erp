package org.example.schoolerp.testsupport;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class DatabaseCleanupExtension implements BeforeEachCallback {

  private static final Set<String> EXCLUDED_TABLES =
      Set.of("flyway_schema_history", "roles", "permissions", "role_permissions");

  @Override
  public void beforeEach(ExtensionContext context) {
    JdbcTemplate jdbcTemplate =
        SpringExtension.getApplicationContext(context).getBean(JdbcTemplate.class);

    List<String> tables =
        jdbcTemplate
            .queryForList(
                """
                SELECT tablename FROM pg_tables
                WHERE schemaname = 'public'
                    AND tablename NOT IN ('flyway_schema_history')
                """,
                String.class)
            .stream()
            .filter(t -> !EXCLUDED_TABLES.contains(t))
            .toList();

    if (!tables.isEmpty()) {
      jdbcTemplate.execute(
          "TRUNCATE TABLE " + String.join(", ", tables) + " RESTART IDENTITY CASCADE");
    }
  }
}
