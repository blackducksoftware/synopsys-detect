/**
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
package com.synopsys.integration.detect.lifecycle.boot.decision;

import com.synopsys.integration.polaris.common.configuration.PolarisServerConfig;

public class PolarisDecision {
    private final boolean shouldRun;
    private final PolarisServerConfig polarisServerConfig;

    public static PolarisDecision skip() {
        return new PolarisDecision(false, null);
    }

    public static PolarisDecision runOnline(final PolarisServerConfig polarisServerConfig) {
        return new PolarisDecision(true, polarisServerConfig);
    }

    public PolarisDecision(final boolean shouldRun, final PolarisServerConfig polarisServerConfig) {
        this.shouldRun = shouldRun;
        this.polarisServerConfig = polarisServerConfig;
    }

    public PolarisServerConfig getPolarisServerConfig() {
        return polarisServerConfig;
    }

    public boolean shouldRun() {
        return shouldRun;
    }
}
