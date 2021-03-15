/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.profiling;

public class Timing<T> {
    private final long ms;
    private final T key;

    public Timing(final T key, final long ms) {
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
