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
package com.synopsys.integration.detect.configuration.connection;

import java.util.List;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.rest.proxy.ProxyInfo;

public class ConnectionDetails {
    private final ProxyInfo proxyInformation; // Not null because of NO_PROXY_INFO value.
    private final List<Pattern> ignoredProxyHostPatterns;
    private final Long timeout;
    private final Boolean alwaysTrust;

    public ConnectionDetails(@NotNull final ProxyInfo proxyInformation, @NotNull final List<Pattern> ignoredProxyHostPatterns, @NotNull final Long timeout, @NotNull final Boolean alwaysTrust) {
        this.proxyInformation = proxyInformation;
        this.ignoredProxyHostPatterns = ignoredProxyHostPatterns;
        this.timeout = timeout;
        this.alwaysTrust = alwaysTrust;
    }

    @NotNull
    public List<Pattern> getIgnoredProxyHostPatterns() {
        return ignoredProxyHostPatterns;
    }

    @NotNull
    public ProxyInfo getProxyInformation() {
        return proxyInformation;
    }

    @NotNull
    public Long getTimeout() {
        return timeout;
    }

    @NotNull
    public Boolean getAlwaysTrust() {
        return alwaysTrust;
    }
}

