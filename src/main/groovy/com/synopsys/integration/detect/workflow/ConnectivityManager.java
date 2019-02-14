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
package com.synopsys.integration.detect.workflow;

import java.util.Optional;

import org.springframework.util.Assert;

import com.synopsys.integration.detect.workflow.phonehome.PhoneHomeManager;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;

public class ConnectivityManager {
    private final boolean isDetectOnline;
    private final BlackDuckServicesFactory blackDuckServicesFactory;
    private final PhoneHomeManager phoneHomeManager;
    private final BlackDuckServerConfig blackDuckServerConfig;

    public static ConnectivityManager offline() {
        return new ConnectivityManager(false, null, null, null);
    }

    public static ConnectivityManager online(BlackDuckServicesFactory blackDuckServicesFactory, final PhoneHomeManager phoneHomeManager, final BlackDuckServerConfig blackDuckServerConfig) {
        Assert.notNull(blackDuckServicesFactory, "Online detect needs a services factory.");
        Assert.notNull(blackDuckServerConfig, "Online detect needs a server config.");
        return new ConnectivityManager(true, blackDuckServicesFactory, phoneHomeManager, blackDuckServerConfig);
    }

    private ConnectivityManager(boolean isDetectOnline, final BlackDuckServicesFactory blackDuckServicesFactory, final PhoneHomeManager phoneHomeManager, BlackDuckServerConfig blackDuckServerConfig) {
        this.isDetectOnline = isDetectOnline;
        this.blackDuckServicesFactory = blackDuckServicesFactory;
        this.phoneHomeManager = phoneHomeManager;
        this.blackDuckServerConfig = blackDuckServerConfig;
    }

    public boolean isDetectOnline() {
        return isDetectOnline;
    }

    public Optional<BlackDuckServicesFactory> getBlackDuckServicesFactory() {
        return Optional.ofNullable(blackDuckServicesFactory);
    }

    public Optional<BlackDuckServerConfig> getBlackDuckServerConfig() {
        return Optional.ofNullable(blackDuckServerConfig);
    }

    public Optional<PhoneHomeManager> getPhoneHomeManager() {
        return Optional.ofNullable(phoneHomeManager);
    }

}
