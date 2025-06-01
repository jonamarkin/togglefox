package com.markin.togglefox.dto.command;

import java.util.Set;

public class UserTargetingStrategyConfig {

    private final Set<String> targetUsers;
    private final boolean whitelist; // true for whitelist, false for blacklist

    public UserTargetingStrategyConfig(Set<String> targetUsers, boolean whitelist) {
        this.targetUsers = Set.copyOf(targetUsers);
        this.whitelist = whitelist;
    }

    public Set<String> getTargetUsers() {
        return targetUsers;
    }
    public boolean isWhitelist() {
        return whitelist;
    }

}
