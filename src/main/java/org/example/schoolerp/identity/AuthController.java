package org.example.schoolerp.identity;

import org.example.schoolerp.identity.repos.AuthAccountRepository;
import org.example.schoolerp.security.auth.JwtService;
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
   
    private final AuthAccountRepository authAccountRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        var authAccount = authAccountRepository.findByUserUsername(request.username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getUsername()));

        String token = jwtService.generateToken(authAccount);
        return ResponseEntity.ok(token);
    }
    
}
