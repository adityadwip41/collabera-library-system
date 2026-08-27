package com.collabera.library.api;

import com.collabera.library.api.dto.*;
import com.collabera.library.service.BorrowerService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/borrowers")
public class BorrowerController {
    private final BorrowerService service;
    public BorrowerController(BorrowerService service) { this.service = service; }

    @PostMapping
    @Operation(summary = "Register a borrower")
    public ResponseEntity<BorrowerResponse> register(@Valid @RequestBody RegisterBorrowerRequest request) {
        BorrowerResponse response = service.register(request);
        return ResponseEntity.created(URI.create("/api/v1/borrowers/" + response.id())).body(response);
    }
}
