package com.markin.togglefox.dto.command;

import java.util.Objects;

public class EnableFlagCommand {

    private final String flagId;

    public EnableFlagCommand(String flagId) {
        this.flagId = Objects.requireNonNull(flagId, "Flag ID cannot be null");
    }

    public String getFlagId() {
        return flagId;
    }

    @Override
    public String toString() {
        return "EnableFlagCommand{flagId='" + flagId + "'}";
    }
}
