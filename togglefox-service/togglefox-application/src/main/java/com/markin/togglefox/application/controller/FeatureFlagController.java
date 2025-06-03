package com.markin.togglefox.application.controller;

import com.markin.togglefox.application.dto.request.CreateFlagRequestDto;
import com.markin.togglefox.application.dto.request.UpdateStrategyRequestDto;
import com.markin.togglefox.application.dto.response.FeatureFlagResponseDto;
import com.markin.togglefox.application.mapper.FeatureFlagDtoMapper;
import com.markin.togglefox.domain.model.FeatureFlag;
import com.markin.togglefox.domain.model.FeatureFlagId;
import com.markin.togglefox.port.in.CreateFlagUseCase;
import com.markin.togglefox.port.in.ManageFlagUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/flags")
@Tag(name = "Feature Flags", description = "Feature flag management API")
public class FeatureFlagController {

    private static final Logger log = LoggerFactory.getLogger(FeatureFlagController.class);
    private final CreateFlagUseCase createFlagUseCase;
    private final ManageFlagUseCase manageFlagUseCase;
    private final FeatureFlagDtoMapper mapper;

    public FeatureFlagController(CreateFlagUseCase createFlagUseCase,
                                 ManageFlagUseCase manageFlagUseCase,
                                 FeatureFlagDtoMapper mapper) {
        this.createFlagUseCase = createFlagUseCase;
        this.manageFlagUseCase = manageFlagUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Create a new feature flag")
    public ResponseEntity<FeatureFlagResponseDto> createFlag(
            @Valid @RequestBody CreateFlagRequestDto request) {

        var command = mapper.toCreateCommand(request);
        FeatureFlag flag = createFlagUseCase.createFlag(command);
        FeatureFlagResponseDto response = mapper.toResponse(flag);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all flags")
    public ResponseEntity<List<FeatureFlagResponseDto>> getAllFlags() {
        List<FeatureFlag> flags = (List<FeatureFlag>) manageFlagUseCase.getAllFlags();
        List<FeatureFlagResponseDto> response = mapper.toResponseList(flags);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{flagId}")
    @Operation(summary = "Get feature flag by ID")
    public ResponseEntity<FeatureFlagResponseDto> getFlag(
            @Parameter(name = "flagId", description = "Feature flag ID", required = true)
            @PathVariable String flagId) {

        log.info("Get feature flag by ID: {}", flagId);

        FeatureFlagId id = FeatureFlagId.of(flagId);
        Optional<FeatureFlag> flag = manageFlagUseCase.getFlag(id);

        return flag.map(f -> ResponseEntity.ok(mapper.toResponse(f)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{flagId}/enable")
    @Operation(summary = "Enable a feature flag")
    public ResponseEntity<Void> enableFlag(
            @Parameter(name = "flagId", description = "Feature flag ID", required = true)
            @PathVariable String flagId) {

        var command = mapper.toEnableCommand(flagId);
        manageFlagUseCase.enableFlag(command);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{flagId}/disable")
    @Operation(summary = "Disable a feature flag")
    public ResponseEntity<Void> disableFlag(
            @Parameter(name = "flagId", description = "Feature flag ID", required = true)
            @PathVariable String flagId) {

        FeatureFlagId id = FeatureFlagId.of(flagId);
        manageFlagUseCase.disableFlag(id);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{flagId}/strategy")
    @Operation(summary = "Update rollout strategy")
    public ResponseEntity<Void> updateStrategy(
            @Parameter(name = "flagId", description = "Feature flag ID", required = true)
            @PathVariable String flagId,
            @Valid @RequestBody UpdateStrategyRequestDto request) {

        var command = mapper.toUpdateStrategyCommand(flagId, request);
        manageFlagUseCase.updateStrategy(command);

        return ResponseEntity.ok().build();
    }
}
