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
package com.synopsys.integration.detect.workflow.report.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorLogReportWriter extends LogReportWriter {
    private final Logger logger;

    public ErrorLogReportWriter() {
        this(LoggerFactory.getLogger(ErrorLogReportWriter.class));
    }

    public ErrorLogReportWriter(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void writeLine(final String line) {
        logger.error(line);
    }

    @Override
    public void writeLine(final String line, final Exception e) {
        logger.error(line, e);
    }
}
