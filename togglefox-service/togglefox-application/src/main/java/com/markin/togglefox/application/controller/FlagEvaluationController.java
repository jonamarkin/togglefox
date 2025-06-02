package com.markin.togglefox.application.controller;

import com.markin.togglefox.domain.model.FlagEvaluationResult;
import com.markin.togglefox.port.in.FlagEvaluationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/evaluate")
@Tag(name = "Flag Evaluation", description = "Feature flag evaluation API")
public class FlagEvaluationController {

    private final FlagEvaluationUseCase evaluationUseCase;
    private final FeatureFlagDtoMapper mapper;

    public FlagEvaluationController(FlagEvaluationUseCase evaluationUseCase,
                                    FeatureFlagDtoMapper mapper) {
        this.evaluationUseCase = evaluationUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/{flagName}")
    @Operation(summary = "Evaluate a feature flag")
    public ResponseEntity<FlagEvaluationResponseDto> evaluateFlag(
            @Parameter(description = "Feature flag name") @PathVariable String flagName,
            @Parameter(description = "Environment") @RequestParam String environment,
            @Valid @RequestBody EvaluationRequestDto request) {

        var query = mapper.toEvaluationQuery(flagName, environment, request);
        FlagEvaluationResult result = evaluationUseCase.evaluateFlag(query);
        FlagEvaluationResponseDto response = mapper.toEvaluationResponse(result);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{flagName}")
    @Operation(summary = "Evaluate a feature flag (GET method for simple cases)")
    public ResponseEntity<FlagEvaluationResponseDto> evaluateFlagSimple(
            @Parameter(description = "Feature flag name") @PathVariable String flagName,
            @Parameter(description = "Environment") @RequestParam String environment,
            @Parameter(description = "User ID") @RequestParam(required = false) String userId) {

        var query = mapper.toEvaluationQuery(flagName, environment, userId);
        FlagEvaluationResult result = evaluationUseCase.evaluateFlag(query);
        FlagEvaluationResponseDto response = mapper.toEvaluationResponse(result);

        return ResponseEntity.ok(response);
    }
}
