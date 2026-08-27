package com.collabera.library.api.dto;

import jakarta.validation.constraints.*;

public record RegisterBookRequest(
        @NotBlank @Pattern(regexp = "(?:\\d{10}|\\d{13})", message = "must contain exactly 10 or 13 digits") String isbn,
        @NotBlank @Size(max = 255) String title,
        @NotBlank @Size(max = 255) String author) {}
