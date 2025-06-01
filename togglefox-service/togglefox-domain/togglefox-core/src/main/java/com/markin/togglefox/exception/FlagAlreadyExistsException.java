package com.markin.togglefox.exception;

public class FlagAlreadyExistsException extends RuntimeException{
    public FlagAlreadyExistsException(String message) {
        super(message);
    }

    public FlagAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
