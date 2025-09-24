package com.example.authservice.controller;

import com.example.authservice.DTO.*;
import com.example.authservice.service.MemberService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/authenticate")
    public ResponseEntity<LoginResponse> signIn(@Valid @RequestBody LoginDTO request) {
        return ResponseEntity.ok(memberService.authenticateUser(request));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> signUp(@Valid @RequestBody SignUpDTO request) {
        return ResponseEntity.ok(memberService.registerUser(request));
    }

    @PutMapping("/activate-account/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> activateAccount(@PathVariable Long id) {
        return ResponseEntity.ok("user : " + memberService.activateUserAccount(id) + " account's has been activated");
    }

    @PostMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("authentication service status : ✅");
    }
}
