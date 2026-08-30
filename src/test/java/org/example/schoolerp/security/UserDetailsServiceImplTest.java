package org.example.schoolerp.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import org.example.schoolerp.identity.entity.*;
import org.example.schoolerp.identity.entity.AuthAccount;
import org.example.schoolerp.identity.repo.AuthAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

  @Mock private AuthAccountRepository authAccountRepository;

  @Mock private AuthAccount account;

  @Mock private User user;

  @Mock private Role adminRole;

  @Mock private Role teacherRole;

  @Mock private Permission readPermission;

  @Mock private Permission writePermission;

  private UserDetailsServiceImpl serviceImpl;

  @BeforeEach
  void setup() {
    serviceImpl = new UserDetailsServiceImpl(authAccountRepository);
  }

  private void givenExistingAccount() {
    when(authAccountRepository.findByUserUsername("john")).thenReturn(Optional.of(account));

    when(account.getUser()).thenReturn(user);
    when(user.getUsername()).thenReturn("john");
    when(account.getPasswordHash()).thenReturn("hashed-password");
    when(account.isLocked()).thenReturn(false);
    when(user.getRoles()).thenReturn(Set.of());
  }

  @Test
  void shouldBuildUserDetailsFromAuthAccount() {
    givenExistingAccount();

    when(user.getRoles()).thenReturn(Set.of(adminRole));
    when(adminRole.getName()).thenReturn("ROLE_ADMIN");
    when(adminRole.getPermissions()).thenReturn(Set.of(readPermission));
    when(readPermission.getName()).thenReturn("USER_READ");

    UserDetails result = serviceImpl.loadUserByUsername("john");

    assertThat(result.getUsername()).isEqualTo("john");
    assertThat(result.getPassword()).isEqualTo("hashed-password");
    assertThat(result.isAccountNonLocked()).isTrue();

    assertThat(result.getAuthorities())
        .extracting(GrantedAuthority::getAuthority)
        .containsExactlyInAnyOrder("ROLE_ADMIN", "USER_READ");
  }

  @Test
  void shouldMarkUserAsLockedWhenAccountIsLocked() {
    givenExistingAccount();

    when(account.isLocked()).thenReturn(true);

    UserDetails result = serviceImpl.loadUserByUsername("john");

    assertThat(result.isAccountNonLocked()).isFalse();
  }

  @Test
  void shouldIncludeAuthoritiesFromAllRoles() {
    givenExistingAccount();

    when(user.getRoles()).thenReturn(Set.of(adminRole, teacherRole));

    when(adminRole.getName()).thenReturn("ROLE_ADMIN");
    when(adminRole.getPermissions()).thenReturn(Set.of());

    when(teacherRole.getName()).thenReturn("ROLE_TEACHER");
    when(teacherRole.getPermissions()).thenReturn(Set.of());

    UserDetails result = serviceImpl.loadUserByUsername("john");

    assertThat(result.getAuthorities())
        .extracting(GrantedAuthority::getAuthority)
        .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_TEACHER");
  }

  @Test
  void shouldIncludeAllPermissionsFromRole() {
    givenExistingAccount();

    when(user.getRoles()).thenReturn(Set.of(adminRole));

    when(adminRole.getName()).thenReturn("ROLE_ADMIN");

    when(adminRole.getPermissions()).thenReturn(Set.of(readPermission, writePermission));

    when(readPermission.getName()).thenReturn("USER_READ");
    when(writePermission.getName()).thenReturn("USER_WRITE");

    UserDetails result = serviceImpl.loadUserByUsername("john");

    assertThat(result.getAuthorities())
        .extracting(GrantedAuthority::getAuthority)
        .containsExactlyInAnyOrder("ROLE_ADMIN", "USER_READ", "USER_WRITE");
  }

  @Test
  void shouldRemoveDuplicateAuthorities() {
    givenExistingAccount();

    when(user.getRoles()).thenReturn(Set.of(adminRole, teacherRole));

    when(adminRole.getName()).thenReturn("ROLE_ADMIN");
    when(adminRole.getPermissions()).thenReturn(Set.of(readPermission));

    when(teacherRole.getName()).thenReturn("ROLE_TEACHER");
    when(teacherRole.getPermissions()).thenReturn(Set.of(readPermission));

    when(readPermission.getName()).thenReturn("USER_READ");

    UserDetails result = serviceImpl.loadUserByUsername("john");

    assertThat(result.getAuthorities())
        .extracting(GrantedAuthority::getAuthority)
        .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_TEACHER", "USER_READ");
  }

  @Test
  void shouldThrowWhenAccountDoesNotExist() {
    when(authAccountRepository.findByUserUsername("john")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> serviceImpl.loadUserByUsername("john"))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessage("No user: john");
  }
}
