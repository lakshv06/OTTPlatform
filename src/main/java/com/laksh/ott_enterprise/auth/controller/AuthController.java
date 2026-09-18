package com.laksh.ott_enterprise.auth.controller;

import com.laksh.ott_enterprise.auth.dto.AuthResponseDTO;
import com.laksh.ott_enterprise.auth.dto.LoginRequestDTO;
import com.laksh.ott_enterprise.auth.dto.SignupRequestDTO;
import com.laksh.ott_enterprise.auth.entity.UserEntity;
import com.laksh.ott_enterprise.auth.enums.UserRoles;
import com.laksh.ott_enterprise.auth.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.laksh.ott_enterprise.auth.service.JwtService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDTO> signup(@Valid @RequestBody SignupRequestDTO request) {

        if (userRepository.existsByEmail(request.email())) {
            return ResponseEntity.badRequest().build();
        }

        UserEntity user = new UserEntity();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName());
        user.setRole(UserRoles.USER);
        userRepository.save(user);
        String token = jwtService.generateAccessToken(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponseDTO(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        UserEntity user = userRepository.findByEmail(request.email()).orElseThrow();
        String token = jwtService.generateAccessToken(user);
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<String>logout(@RequestHeader("Authorization") String authHeader){
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            jwtService.invalidateToken(token);
        }
        return ResponseEntity.ok("Logged out successfully");
    }
}
