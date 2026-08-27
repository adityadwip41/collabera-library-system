package com.collabera.library.api.dto;

import jakarta.validation.constraints.*;

public record RegisterBorrowerRequest(
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Email @Size(max = 320) String email) {}
