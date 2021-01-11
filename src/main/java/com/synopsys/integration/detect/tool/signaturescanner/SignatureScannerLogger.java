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
package com.synopsys.integration.detect.tool.signaturescanner;

import org.slf4j.Logger;

import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.LogLevel;

public class SignatureScannerLogger extends IntLogger {

    private final Logger logger;

    public SignatureScannerLogger(final Logger logger) {
        this.logger = logger;
    }

    private boolean shouldInfo(final String line) {
        //redirect sig scan INFO level to DEBUG.
        return !line.contains("INFO: ");
    }

    @Override
    public void alwaysLog(final String txt) {
        logger.info(txt);
    }

    @Override
    public void info(final String txt) {
        if (shouldInfo(txt)) {
            logger.info(txt);
        } else {
            debug(txt);
        }
    }

    @Override
    public void error(final Throwable t) {
        logger.error("", t);
    }

    @Override
    public void error(final String txt, final Throwable t) {
        logger.error(txt, t);

    }

    @Override
    public void error(final String txt) {
        logger.error(txt);

    }

    @Override
    public void warn(final String txt) {
        logger.warn(txt);

    }

    @Override
    public void trace(final String txt) {
        logger.trace(txt);

    }

    @Override
    public void trace(final String txt, final Throwable t) {
        logger.trace(txt, t);

    }

    @Override
    public void debug(final String txt) {
        logger.debug(txt);

    }

    @Override
    public void debug(final String txt, final Throwable t) {
        logger.debug(txt, t);
    }

    @Override
    public void setLogLevel(final LogLevel logLevel) {
        // TODO: Actually do something?
    }

    @Override
    public LogLevel getLogLevel() {
        return LogLevel.DEBUG;
    }
}
