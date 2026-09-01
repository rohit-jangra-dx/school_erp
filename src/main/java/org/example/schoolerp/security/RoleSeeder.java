package org.example.schoolerp.security;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.schoolerp.identity.entity.Role;
import org.example.schoolerp.identity.repo.RoleRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleSeeder implements ApplicationRunner {

  private final RoleRepository roleRepository;

  private static final List<String> DEFAULT_ROLES =
      List.of("ROLE_USER", "ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT");

  @Override
  public void run(@NonNull ApplicationArguments args) {
    for (String roleName : DEFAULT_ROLES) {
      roleRepository.findByName(roleName).orElseGet(() -> roleRepository.save(new Role(roleName)));
    }
  }
}
