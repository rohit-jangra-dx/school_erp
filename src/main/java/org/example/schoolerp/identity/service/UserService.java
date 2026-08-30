package org.example.schoolerp.identity.service;

import lombok.RequiredArgsConstructor;
import org.example.schoolerp.identity.entity.User;
import org.example.schoolerp.identity.repo.RoleRepository;
import org.example.schoolerp.identity.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  @Transactional
  public User create(String username, String roleName) {
    var role =
        roleRepository
            .findByName(roleName)
            .orElseThrow(() -> new IllegalArgumentException("No role found: " + roleName));

    var user = new User(username);
    user.addRole(role);
    return userRepository.save(user);
  }
}
