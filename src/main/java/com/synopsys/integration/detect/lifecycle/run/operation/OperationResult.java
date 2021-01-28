package com.synopsys.integration.detect.lifecycle.run.operation;

import java.util.Optional;

public class OperationResult<T> {
    private T content;
    private boolean success;

    public static <T> OperationResult<T> of(OperationResult<T> result) {
        return new OperationResult<>(result.getContent().orElse(null), result.hasSucceeded());
    }

    public static <T> OperationResult<T> success(T content) {
        return new OperationResult<>(content, true);
    }

    public static <T> OperationResult<T> success() {
        return new OperationResult<T>(null, true);
    }

    public static <T> OperationResult<T> fail(T content) {
        return new OperationResult<>(content, false);
    }

    public static <T> OperationResult<T> fail() {
        return new OperationResult<>(null, false);
    }

    private OperationResult(T content, boolean success) {
        this.success = success;
        this.content = content;
    }

    public Optional<T> getContent() {
        return Optional.ofNullable(content);
    }

    public boolean hasFailed() {
        return !success;
    }

    public boolean hasSucceeded() {
        return success;
    }

    public boolean hasContent() {
        return null != content;
    }
}
