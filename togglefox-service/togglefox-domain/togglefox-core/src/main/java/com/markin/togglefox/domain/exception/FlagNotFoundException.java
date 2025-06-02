package com.markin.togglefox.domain.exception;

public class FlagNotFoundException extends RuntimeException {
    public FlagNotFoundException(String flagName) {
        super("Flag not found: " + flagName);
    }

    public FlagNotFoundException(String flagName, Throwable cause) {
        super("Flag not found: " + flagName, cause);
    }
}
