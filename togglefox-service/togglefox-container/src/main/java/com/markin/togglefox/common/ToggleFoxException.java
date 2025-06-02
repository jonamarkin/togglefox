package com.markin.togglefox.common;

public class ToggleFoxException extends RuntimeException {
    private final String errorCode;

    public ToggleFoxException(String message) {
        super(message);
        this.errorCode = "TOGGLEMATE_ERROR";
    }

    public ToggleFoxException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ToggleFoxException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "TOGGLEMATE_ERROR";
    }

    public ToggleFoxException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "ToggleFoxException{" +
                "errorCode='" + errorCode + '\'' +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}
