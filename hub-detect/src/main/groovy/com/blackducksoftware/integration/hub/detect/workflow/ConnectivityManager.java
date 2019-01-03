/**
 * hub-detect
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
package com.blackducksoftware.integration.hub.detect.workflow;

import java.util.Optional;

import org.springframework.util.Assert;

import com.blackducksoftware.integration.hub.detect.hub.HubServiceManager;
import com.blackducksoftware.integration.hub.detect.workflow.phonehome.PhoneHomeManager;

public class ConnectivityManager {
    private final boolean isDetectOnline;
    private final HubServiceManager hubServiceManager;
    private final PhoneHomeManager phoneHomeManager;

    public static ConnectivityManager offline() {
        return new ConnectivityManager(false, null, null);
    }

    public static ConnectivityManager online(HubServiceManager hubServiceManager, final PhoneHomeManager phoneHomeManager) {
        Assert.notNull(hubServiceManager, "Online detect needs a valid hub services manager.");
        return new ConnectivityManager(true, hubServiceManager, phoneHomeManager);
    }

    private ConnectivityManager(boolean isDetectOnline, final HubServiceManager hubServiceManager, final PhoneHomeManager phoneHomeManager) {
        this.isDetectOnline = isDetectOnline;
        this.hubServiceManager = hubServiceManager;
        this.phoneHomeManager = phoneHomeManager;
    }

    public boolean isDetectOnline() {
        return isDetectOnline;
    }

    public Optional<HubServiceManager> getHubServiceManager() {
        return Optional.ofNullable(hubServiceManager);
    }

    public Optional<PhoneHomeManager> getPhoneHomeManager() {
        return Optional.ofNullable(phoneHomeManager);
    }

}
