/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticSystem;
import com.synopsys.integration.detect.workflow.phonehome.PhoneHomeManager;

public class ShutdownDecision {
    @Nullable
    private final PhoneHomeManager phoneHomeManager; //If provided, will be ended.
    @Nullable
    private final DiagnosticSystem diagnosticSystem; //If provided, will be finished.
    @NotNull
    private final CleanupDecision cleanupDecision;

    public ShutdownDecision(final @Nullable PhoneHomeManager phoneHomeManager, final @Nullable DiagnosticSystem diagnosticSystem,
        final @NotNull CleanupDecision cleanupDecision) {
        this.phoneHomeManager = phoneHomeManager;
        this.diagnosticSystem = diagnosticSystem;
        this.cleanupDecision = cleanupDecision;
    }

    public PhoneHomeManager getPhoneHomeManager() {
        return phoneHomeManager;
    }

    public DiagnosticSystem getDiagnosticSystem() {
        return diagnosticSystem;
    }

    public CleanupDecision getCleanupDecision() {
        return cleanupDecision;
    }
}
