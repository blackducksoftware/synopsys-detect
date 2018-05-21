/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.extraction.bomtool.nuget;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyException;
import com.blackducksoftware.integration.hub.detect.strategy.result.FilesNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.InspectorNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.PassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.StrategyResult;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class NugetProjectStrategy extends Strategy<NugetInspectorContext, NugetInspectorExtractor> {
    static final String[] SUPPORTED_PROJECT_PATTERNS = new String[] {
            //C#
            "*.csproj",
            //F#
            "*.fsproj",
            //VB
            "*.vbproj",
            //Azure Stream Analytics
            "*.asaproj",
            //Docker Compose
            "*.dcproj",
            //Shared Projects
            "*.shproj",
            //Cloud Computing
            "*.ccproj",
            //Fabric Application
            "*.sfproj",
            //Node.js
            "*.njsproj",
            //VC++
            "*.vcxproj",
            //VC++
            "*.vcproj",
            //.NET Core
            "*.xproj",
            //Python
            "*.pyproj",
            //Hive
            "*.hiveproj",
            //Pig
            "*.pigproj",
            //JavaScript
            "*.jsproj",
            //U-SQL
            "*.usqlproj",
            //Deployment
            "*.deployproj",
            //Common Project System Files
            "*.msbuildproj",
            //SQL
            "*.sqlproj",
            //SQL Project Files
            "*.dbproj",
            //RStudio
            "*.rproj"
    };

    @Autowired
    public DetectFileFinder fileFinder;

    @Autowired
    public NugetInspectorManager nugetInspectorManager;

    @Autowired
    public DetectConfiguration detectConfiguration;

    public NugetProjectStrategy() {
        super("Project", BomToolType.NUGET, NugetInspectorContext.class, NugetInspectorExtractor.class);
    }

    @Override
    public StrategyResult applicable(final StrategyEnvironment environment, final NugetInspectorContext context) {
        for (final String filepattern : SUPPORTED_PROJECT_PATTERNS) {
            if (fileFinder.findFile(environment.getDirectory(), filepattern) != null) {
                return new PassedStrategyResult();
            }
        }
        return new FilesNotFoundStrategyResult(SUPPORTED_PROJECT_PATTERNS);
    }

    @Override
    public StrategyResult extractable(final StrategyEnvironment environment, final NugetInspectorContext context) throws StrategyException {
        context.inspectorExe = nugetInspectorManager.findNugetInspector(environment);

        if (context.inspectorExe == null) {
            return new InspectorNotFoundStrategyResult("nuget");
        }

        return new PassedStrategyResult();
    }


}
