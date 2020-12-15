/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detect.lifecycle.shutdown;

import java.io.File;

import org.jetbrains.annotations.Nullable;

public class CleanupDecision {
    private final boolean shouldCleanup;
    private final boolean shouldPreserveScan;
    private final boolean shouldPreserveBdio;
    private final boolean shouldPreserveAirGap;

    @Nullable
    private final File airGapZip;

    public CleanupDecision(final boolean shouldCleanup, final boolean shouldPreserveScan, final boolean shouldPreserveBdio, final boolean shouldPreserveAirGap, final @Nullable File airGapZip) {
        this.shouldCleanup = shouldCleanup;
        this.shouldPreserveScan = shouldPreserveScan;
        this.shouldPreserveBdio = shouldPreserveBdio;
        this.shouldPreserveAirGap = shouldPreserveAirGap;
        this.airGapZip = airGapZip;
    }

    public static CleanupDecision skip() {
        return new CleanupDecision(false, false, false, false, null);
    }

    public boolean shouldCleanup() {
        return shouldCleanup;
    }

    public boolean shouldPreserveScan() {
        return shouldPreserveScan;
    }

    public boolean shouldPreserveBdio() {
        return shouldPreserveBdio;
    }

    public boolean shouldPreserveAirGap() {
        return shouldPreserveAirGap;
    }

    public File getAirGapZip() {
        return airGapZip;
    }
}
