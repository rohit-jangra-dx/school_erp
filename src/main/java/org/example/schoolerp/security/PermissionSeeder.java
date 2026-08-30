package org.example.schoolerp.security;

import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.example.schoolerp.identity.entity.Permission;
import org.example.schoolerp.identity.repo.PermissionRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PermissionSeeder implements ApplicationRunner {

  private final PermissionScanner permissionScanner;
  private final PermissionRepository permissionRepository;

  @Override
  @Transactional
  public void run(ApplicationArguments args) throws Exception {

    Set<String> permissions = permissionScanner.scan();

    for (String permission : permissions) {

      if (!permissionRepository.existsByName(permission)) {
        permissionRepository.save(new Permission(permission));
      }
    }
  }
}
