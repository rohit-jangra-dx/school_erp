package org.example.schoolerp.identity;

import java.util.UUID;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.schoolerp.identity.service.RegisterationService;
import org.example.schoolerp.security.tenant.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
  private static final String USER_ROLE = "ROLE_USER";
  private final AuthenticationManager authenticationManager;
  private final RegisterationService registerationService;

  @Data
  public static class LoginRequest {
    private UUID organizationId;
    private String username;
    private String password;
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody LoginRequest request) {

    // The second case where u need to manually feed the organizationId
    TenantContext.set(request.getOrganizationId());

    try {

      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

      var registerationResult =
          registerationService.registerAndAuthenticate(
              request.getUsername(), request.getPassword(), USER_ROLE);
      return ResponseEntity.ok(registerationResult.token());

    } finally {
      TenantContext.clear();
    }
  }
}
