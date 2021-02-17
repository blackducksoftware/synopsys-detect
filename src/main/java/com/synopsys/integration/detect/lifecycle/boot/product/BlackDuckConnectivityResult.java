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
package com.synopsys.integration.detect.lifecycle.boot.product;

import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;

public class BlackDuckConnectivityResult {
    private boolean successfullyConnected;

    //if failure, the following is populated
    private String failureReason;

    //if success, the following is populated
    private BlackDuckServicesFactory blackDuckServicesFactory;
    private BlackDuckServerConfig blackDuckServerConfig;

    private BlackDuckConnectivityResult(final boolean successfullyConnected, final String failureReason,
        final BlackDuckServicesFactory blackDuckServicesFactory, final BlackDuckServerConfig blackDuckServerConfig) {
        this.successfullyConnected = successfullyConnected;
        this.failureReason = failureReason;
        this.blackDuckServicesFactory = blackDuckServicesFactory;
        this.blackDuckServerConfig = blackDuckServerConfig;
    }

    public static BlackDuckConnectivityResult success(final BlackDuckServicesFactory blackDuckServicesFactory, final BlackDuckServerConfig blackDuckServerConfig) {
        return new BlackDuckConnectivityResult(true, null, blackDuckServicesFactory, blackDuckServerConfig);
    }

    public static BlackDuckConnectivityResult failure(String reason) {
        return new BlackDuckConnectivityResult(false, reason, null, null);
    }

    public boolean isSuccessfullyConnected() {
        return successfullyConnected;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public BlackDuckServicesFactory getBlackDuckServicesFactory() {
        return blackDuckServicesFactory;
    }

    public BlackDuckServerConfig getBlackDuckServerConfig() {
        return blackDuckServerConfig;
    }
}
