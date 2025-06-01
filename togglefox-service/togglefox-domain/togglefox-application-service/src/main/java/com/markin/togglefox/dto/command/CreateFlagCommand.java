package com.markin.togglefox.dto.command;

import com.markin.togglefox.model.Environment;

import java.util.Objects;

public record CreateFlagCommand(
        String name,
        String description,
        Environment environment,
        String createdBy
) {
    public CreateFlagCommand {
        Objects.requireNonNull(name, "Name cannot be null");
        Objects.requireNonNull(environment, "Environment cannot be null");
        Objects.requireNonNull(createdBy, "Created by cannot be null");

        // Validation logic
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Name cannot exceed 100 characters");
        }

        // Normalize the name
        name = name.trim();
    }
}
