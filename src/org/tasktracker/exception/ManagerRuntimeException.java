package org.tasktracker.exception;

public class ManagerRuntimeException extends RuntimeException{
    public ManagerRuntimeException(final String message, Throwable cause) {
        super(message, cause);
    }
}
