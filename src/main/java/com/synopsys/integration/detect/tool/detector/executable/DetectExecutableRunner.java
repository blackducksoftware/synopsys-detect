/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detect.tool.detector.executable;

import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detectable.detectable.executable.Executable;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;

public class DetectExecutableRunner extends SimpleExecutableRunner {
    private final EventSystem eventSystem;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final boolean shouldLogOutput;

    public DetectExecutableRunner(final Consumer<String> outputConsumer, final Consumer<String> traceConsumer, EventSystem eventSystem, boolean shouldLogOutput) {
        super(outputConsumer, traceConsumer);
        this.eventSystem = eventSystem;
        this.shouldLogOutput = shouldLogOutput;
    }

    public static DetectExecutableRunner newDebug(EventSystem eventSystem) {
        Logger logger = LoggerFactory.getLogger(SimpleExecutableRunner.class);
        return new DetectExecutableRunner(logger::debug, logger::trace, eventSystem, true);
    }

    public static DetectExecutableRunner newInfo(EventSystem eventSystem) {
        Logger logger = LoggerFactory.getLogger(SimpleExecutableRunner.class);
        return new DetectExecutableRunner(logger::info, logger::trace, eventSystem, false);
    }

    @Override
    public ExecutableOutput execute(final Executable executable) throws ExecutableRunnerException {
        final ExecutableOutput output = super.execute(executable);
        eventSystem.publishEvent(Event.Executable, output);
        if (output.getReturnCode() != 0 && shouldLogOutput && !logger.isDebugEnabled() && !logger.isTraceEnabled()) {
            if (StringUtils.isNotBlank(output.getStandardOutput())) {
                logger.info("Standard Output: ");
                logger.info(output.getStandardOutput());
            }

            if (StringUtils.isNotBlank(output.getErrorOutput())) {
                logger.info("Error Output: ");
                logger.info(output.getErrorOutput());
            }
        }
        return output;
    }
}
