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

public class ProductDecision {
    private final BlackDuckDecision blackDuckDecision;
    private final PolarisDecision polarisDecision;

    public ProductDecision(final BlackDuckDecision blackDuckDecision, final PolarisDecision polarisDecision) {
        this.blackDuckDecision = blackDuckDecision;
        this.polarisDecision = polarisDecision;
    }

    public BlackDuckDecision getBlackDuckDecision() {
        return blackDuckDecision;
    }

    public PolarisDecision getPolarisDecision() {
        return polarisDecision;
    }

    public boolean willRunAny() {
        return blackDuckDecision.shouldRun() || polarisDecision.shouldRun();
    }
}
