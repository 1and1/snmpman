package com.oneandone.snmpman.exception;

/** Exception type defines the failed initialization of the application. */
public class InitializationException extends RuntimeException {

    /** The version number of this class. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception.
     *
     * @param message the detail message
     * @param cause the exception cause
     */
    public InitializationException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
}
