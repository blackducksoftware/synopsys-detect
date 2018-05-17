package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation;

public class StrategyException extends Exception {
    private static final long serialVersionUID = 3255014283653810035L;
    public StrategyException() { super(); }
    public StrategyException(final String message) { super(message); }
    public StrategyException(final String message, final Throwable cause) { super(message, cause); }
    public StrategyException(final Throwable cause) { super(cause); }
}