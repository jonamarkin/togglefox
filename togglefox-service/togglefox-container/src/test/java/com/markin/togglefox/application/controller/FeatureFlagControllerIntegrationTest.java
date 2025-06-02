package com.markin.togglefox.application.controller;

import com.markin.togglefox.application.dto.request.CreateFlagRequestDto;
import com.markin.togglefox.application.dto.request.EvaluationRequestDto;
import com.markin.togglefox.application.dto.request.UpdateStrategyRequestDto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@Transactional
public class FeatureFlagControllerIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("togglemate_test")
            .withUsername("test")
            .withPassword("test");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("Feature Flag Creation")
    class FlagCreation {

        @Test
        @DisplayName("Should create feature flag successfully")
        void shouldCreateFeatureFlagSuccessfully() throws Exception {
            // Given
            Map<String, Object> strategyConfig = new HashMap<>();
            strategyConfig.put("percentage", 25);

            CreateFlagRequestDto request = new CreateFlagRequestDto(
                    "test-flag",
                    "Test flag description",
                    "development",
                    "PERCENTAGE",
                    strategyConfig
            );

            // When & Then
            mockMvc.perform(post("/api/v1/flags")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("test-flag"))
                    .andExpect(jsonPath("$.description").value("Test flag description"))
                    .andExpect(jsonPath("$.environment").value("development"))
                    .andExpect(jsonPath("$.enabled").value(false))
                    .andExpect(jsonPath("$.strategyType").value("PERCENTAGE"))
                    .andExpect(jsonPath("$.strategyConfig.percentage").value(25))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.createdAt").exists())
                    .andExpect(jsonPath("$.updatedAt").exists());
        }

        @Test
        @DisplayName("Should reject invalid flag name")
        void shouldRejectInvalidFlagName() throws Exception {
            // Given
            Map<String, Object> strategyConfig = new HashMap<>();
            strategyConfig.put("percentage", 25);

            CreateFlagRequestDto request = new CreateFlagRequestDto(
                    "invalid flag name", // Spaces not allowed
                    "Test flag description",
                    "development",
                    "PERCENTAGE",
                    strategyConfig
            );

            // When & Then
            mockMvc.perform(post("/api/v1/flags")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors.name").exists());
        }

        @Test
        @DisplayName("Should reject duplicate flag in same environment")
        void shouldRejectDuplicateFlagInSameEnvironment() throws Exception {
            // Given
            Map<String, Object> strategyConfig = new HashMap<>();
            strategyConfig.put("percentage", 25);

            CreateFlagRequestDto request = new CreateFlagRequestDto(
                    "duplicate-flag",
                    "Test flag description",
                    "development",
                    "PERCENTAGE",
                    strategyConfig
            );

            // Create first flag
            mockMvc.perform(post("/api/v1/flags")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());

            // When & Then - Try to create duplicate
            mockMvc.perform(post("/api/v1/flags")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("already exists")));
        }
    }

    @Nested
    @DisplayName("Feature Flag Management")
    class FlagManagement {

        @Test
        @DisplayName("Should enable and disable flag")
        void shouldEnableAndDisableFlag() throws Exception {
            // Given - Create a flag first
            String flagId = createTestFlag("enable-test-flag");

            // When & Then - Enable flag
            mockMvc.perform(put("/api/v1/flags/{flagId}/enable", flagId))
                    .andExpect(status().isOk());

            // Verify flag is enabled
            mockMvc.perform(get("/api/v1/flags/{flagId}", flagId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.enabled").value(true));

            // When & Then - Disable flag
            mockMvc.perform(put("/api/v1/flags/{flagId}/disable", flagId))
                    .andExpect(status().isOk());

            // Verify flag is disabled
            mockMvc.perform(get("/api/v1/flags/{flagId}", flagId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.enabled").value(false));
        }

        @Test
        @DisplayName("Should update rollout strategy")
        void shouldUpdateRolloutStrategy() throws Exception {
            // Given - Create a flag first
            String flagId = createTestFlag("strategy-test-flag");

            Map<String, Object> newStrategyConfig = new HashMap<>();
            newStrategyConfig.put("users", List.of("user1", "user2", "admin"));

            UpdateStrategyRequestDto request = new UpdateStrategyRequestDto(
                    "USER_TARGETING",
                    newStrategyConfig
            );

            // When
            mockMvc.perform(put("/api/v1/flags/{flagId}/strategy", flagId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            // Then - Verify strategy was updated
            mockMvc.perform(get("/api/v1/flags/{flagId}", flagId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.strategyType").value("USER_TARGETING"))
                    .andExpect(jsonPath("$.strategyConfig.users").isArray())
                    .andExpect(jsonPath("$.strategyConfig.users").value(containsInAnyOrder("user1", "user2", "admin")));
        }

        @Test
        @DisplayName("Should return 404 for non-existent flag")
        void shouldReturn404ForNonExistentFlag() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/flags/non-existent-id"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Feature Flag Evaluation")
    class FlagEvaluation {

        @Test
        @DisplayName("Should evaluate flag with percentage strategy")
        void shouldEvaluateFlagWithPercentageStrategy() throws Exception {
            // Given - Create and enable a flag
            String flagId = createTestFlag("eval-test-flag");
            mockMvc.perform(put("/api/v1/flags/{flagId}/enable", flagId));

            // When & Then - Evaluate flag
            mockMvc.perform(get("/api/v1/evaluate/eval-test-flag")
                            .param("environment", "development")
                            .param("userId", "user123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.flagId").value(flagId))
                    .andExpect(jsonPath("$.enabled").isBoolean())
                    .andExpect(jsonPath("$.reason").exists());
        }

        @Test
        @DisplayName("Should evaluate flag with attributes")
        void shouldEvaluateFlagWithAttributes() throws Exception {
            // Given - Create flag with attribute-based strategy
            String flagId = createFlagWithAttributeStrategy("attr-test-flag");
            mockMvc.perform(put("/api/v1/flags/{flagId}/enable", flagId));

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("country", "US");
            attributes.put("plan", "premium");

            EvaluationRequestDto request = new EvaluationRequestDto("user123", attributes);

            // When & Then
            mockMvc.perform(post("/api/v1/evaluate/attr-test-flag")
                            .param("environment", "development")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.flagId").value(flagId))
                    .andExpect(jsonPath("$.enabled").value(true))
                    .andExpect(jsonPath("$.reason").value(containsString("All attribute rules matched")));
        }

        @Test
        @DisplayName("Should return disabled for non-existent flag")
        void shouldReturnDisabledForNonExistentFlag() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/evaluate/non-existent-flag")
                            .param("environment", "development")
                            .param("userId", "user123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.enabled").value(false))
                    .andExpect(jsonPath("$.reason").value(containsString("not found")));
        }
    }

    private String createTestFlag(String flagName) throws Exception {
        Map<String, Object> strategyConfig = new HashMap<>();
        strategyConfig.put("percentage", 50);

        CreateFlagRequestDto request = new CreateFlagRequestDto(
                flagName,
                "Test flag description",
                "development",
                "PERCENTAGE",
                strategyConfig
        );

        String response = mockMvc.perform(post("/api/v1/flags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var responseMap = objectMapper.readValue(response, Map.class);
        return (String) responseMap.get("id");
    }

    private String createFlagWithAttributeStrategy(String flagName) throws Exception {
        Map<String, Object> rules = new HashMap<>();
        rules.put("country", List.of("US", "CA"));
        rules.put("plan", List.of("premium", "enterprise"));

        Map<String, Object> strategyConfig = new HashMap<>();
        strategyConfig.put("rules", rules);

        CreateFlagRequestDto request = new CreateFlagRequestDto(
                flagName,
                "Test flag with attributes",
                "development",
                "ATTRIBUTE_BASED",
                strategyConfig
        );

        String response = mockMvc.perform(post("/api/v1/flags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var responseMap = objectMapper.readValue(response, Map.class);
        return (String) responseMap.get("id");
    }
}
