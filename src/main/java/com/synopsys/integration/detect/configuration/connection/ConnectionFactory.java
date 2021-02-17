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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.common.util.ProxyUtil;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.SilentIntLogger;
import com.synopsys.integration.rest.client.IntHttpClient;
import com.synopsys.integration.rest.proxy.ProxyInfo;

public class ConnectionFactory {
    private final ConnectionDetails connectionDetails;

    public ConnectionFactory(final ConnectionDetails connectionDetails) {
        this.connectionDetails = connectionDetails;
    }

    public IntHttpClient createConnection(@NotNull String url, @Nullable IntLogger logger) {
        if (logger == null) {
            logger = new SilentIntLogger();
        }
        if (ProxyUtil.shouldIgnoreUrl(url, connectionDetails.getIgnoredProxyHostPatterns(), logger)) {
            return new IntHttpClient(logger, Math.toIntExact(connectionDetails.getTimeout()), connectionDetails.getAlwaysTrust(), ProxyInfo.NO_PROXY_INFO);
        } else {
            return new IntHttpClient(logger, Math.toIntExact(connectionDetails.getTimeout()), connectionDetails.getAlwaysTrust(), connectionDetails.getProxyInformation());
        }

    }
}