package org.example.schoolerp.identity;

import java.util.UUID;

import org.example.schoolerp.security.tenant.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
public class AuthController {
   
    private final AuthenticationManager authenticationManager;
    private final AuthService authService;

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
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            var token =  authService.generateTokenFromUsername(request.getUsername());
            return ResponseEntity.ok(token);
            
        } finally {
            TenantContext.clear();
        }
        

    }
    
}
