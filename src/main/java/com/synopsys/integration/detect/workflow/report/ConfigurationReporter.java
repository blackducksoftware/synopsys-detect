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
package com.synopsys.integration.detect.workflow.report;

import java.util.List;

import com.synopsys.integration.detect.DetectInfo;
import com.synopsys.integration.detect.help.DetectOption;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;

public class ConfigurationReporter {
    public void writeReport(final ReportWriter writer, final DetectInfo detectInfo, final List<DetectOption> detectOptions) {
        writer.writeSeparator();
        writer.writeLine("Detect Info");
        writer.writeSeparator();
        writer.writeLine("Detect Version: " + detectInfo.getDetectVersion());
        writer.writeLine("Operating System: " + detectInfo.getCurrentOs());
        writer.writeSeparator();
        writer.writeLine("Detect Configuration");
        writer.writeSeparator();
        //TODO: Replace
        //final DetectConfigurationReporter detectConfigurationReporter = new DetectConfigurationReporter();
        //detectConfigurationReporter.print(writer, writer, detectOptions);
        //writer.writeSeparator();
    }
}
