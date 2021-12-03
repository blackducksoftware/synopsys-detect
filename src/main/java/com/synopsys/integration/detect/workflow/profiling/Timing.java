package com.synopsys.integration.detect.workflow.profiling;

public class Timing<T> {
    private final long ms;
    private final T key;

    public Timing(T key, long ms) {
        this.ms = ms;
        this.key = key;
    }

    public long getMs() {
        return ms;
    }

    public T getKey() {
        return key;
    }
}
