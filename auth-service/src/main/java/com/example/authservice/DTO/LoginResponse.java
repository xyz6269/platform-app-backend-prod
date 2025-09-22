package com.example.authservice.DTO;

import jakarta.validation.constraints.NotBlank;

public record LoginResponse(
        @NotBlank
        String token
) {}
