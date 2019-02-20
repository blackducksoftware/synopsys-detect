/**
 * synopsys-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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

import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;

public class BlackDuckDecision {
    private boolean shouldRun;
    private boolean successfullyConnected;
    private String connectionFailureReason;
    private boolean isOffline;

    private BlackDuckServicesFactory blackDuckServicesFactory;
    private BlackDuckServerConfig blackDuckServerConfig;

    public static BlackDuckDecision skip() {
        return new BlackDuckDecision(false, false, null, true, null, null);
    }

    public static BlackDuckDecision runOffline() {
        return new BlackDuckDecision(true, false, null, true, null, null);
    }

    public static BlackDuckDecision runOnlineConnected(final BlackDuckServicesFactory blackDuckServicesFactory,
        final BlackDuckServerConfig blackDuckServerConfig) {
        return new BlackDuckDecision(true, true, null, false, blackDuckServicesFactory, blackDuckServerConfig);
    }

    public static BlackDuckDecision runOnlineDisconnected(String reason) {
        return new BlackDuckDecision(true, false, reason, false, null, null);
    }

    public BlackDuckDecision(final boolean shouldRun, final boolean successfullyConnected, final String connectionFailureReason, final boolean isOffline, final BlackDuckServicesFactory blackDuckServicesFactory,
        final BlackDuckServerConfig blackDuckServerConfig) {
        this.shouldRun = shouldRun;
        this.successfullyConnected = successfullyConnected;
        this.connectionFailureReason = connectionFailureReason;
        this.isOffline = isOffline;
        this.blackDuckServicesFactory = blackDuckServicesFactory;
        this.blackDuckServerConfig = blackDuckServerConfig;
    }

    public BlackDuckServicesFactory getBlackDuckServicesFactory() {
        return blackDuckServicesFactory;
    }

    public BlackDuckServerConfig getBlackDuckServerConfig() {
        return blackDuckServerConfig;
    }

    public boolean isSuccessfullyConnected() {
        return successfullyConnected;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public boolean shouldRun() {
        return shouldRun;
    }

    public String getConnectionFailureReason(){
        return connectionFailureReason;
    }
}
