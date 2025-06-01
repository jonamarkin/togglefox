package com.markin.togglefox.dto.command;

import com.markin.togglefox.model.Environment;

import java.util.Objects;

public class CreateFlagCommand {
    private String name;
    private String description;
    private Environment environment;
    private String createdBy;

    public CreateFlagCommand(String name, String description, Environment environment, String createdBy) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.description = description;
        this.environment = Objects.requireNonNull(environment, "Environment cannot be null");
        this.createdBy = Objects.requireNonNull(createdBy, "CreatedBy cannot be null");
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public String getCreatedBy() {
        return createdBy;
    }

}
