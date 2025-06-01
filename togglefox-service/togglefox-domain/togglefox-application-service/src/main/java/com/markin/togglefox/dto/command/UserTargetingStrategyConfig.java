package com.markin.togglefox.dto.command;

import java.util.Set;

public record UserTargetingStrategyConfig(
        Set<String> targetUsers,
        boolean whitelist
) {

    public UserTargetingStrategyConfig {
        if (targetUsers == null || targetUsers.isEmpty()) {
            throw new IllegalArgumentException("Target users cannot be null or empty");
        }
        targetUsers = Set.copyOf(targetUsers);
    }

}
