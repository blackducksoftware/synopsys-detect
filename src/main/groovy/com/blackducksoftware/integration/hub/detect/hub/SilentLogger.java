/**
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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
package com.blackducksoftware.integration.hub.detect.hub;

import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.log.LogLevel;

/**
 * @deprecated Please use the SilentLogger from integration-common 7.2+ (once available)
 */
@Deprecated
public class SilentLogger extends IntLogger {
    @Override
    public void alwaysLog(final String arg0) {
    }

    @Override
    public void debug(final String arg0, final Throwable arg1) {
    }

    @Override
    public void debug(final String arg0) {
    }

    @Override
    public void error(final String arg0, final Throwable arg1) {
    }

    @Override
    public void error(final String arg0) {
    }

    @Override
    public void error(final Throwable arg0) {
    }

    @Override
    public LogLevel getLogLevel() {
        return LogLevel.OFF;
    }

    @Override
    public void info(final String arg0) {
    }

    @Override
    public void setLogLevel(final LogLevel arg0) {
    }

    @Override
    public void trace(final String arg0, final Throwable arg1) {
    }

    @Override
    public void trace(final String arg0) {
    }

    @Override
    public void warn(final String arg0) {
    }

}
