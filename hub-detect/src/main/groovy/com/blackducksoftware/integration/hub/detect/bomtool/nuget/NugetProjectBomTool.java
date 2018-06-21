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
package com.blackducksoftware.integration.hub.detect.bomtool.nuget;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.bomtool.result.BomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.FilesNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.InspectorNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.PassedBomToolResult;
import com.blackducksoftware.integration.hub.detect.evaluation.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.evaluation.BomToolException;
import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction;
import com.blackducksoftware.integration.hub.detect.model.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

public class NugetProjectBomTool extends BomTool {
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

    private final DetectFileFinder fileFinder;
    private final NugetInspectorManager nugetInspectorManager;
    private final NugetInspectorExtractor nugetInspectorExtractor;

    private String inspectorExe;

    public NugetProjectBomTool(final BomToolEnvironment environment, final DetectFileFinder fileFinder, final NugetInspectorManager nugetInspectorManager, final NugetInspectorExtractor nugetInspectorExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.nugetInspectorExtractor = nugetInspectorExtractor;
        this.nugetInspectorManager = nugetInspectorManager;
    }

    @Override
    public BomToolResult applicable() {
        for (final String filepattern : SUPPORTED_PROJECT_PATTERNS) {
            if (fileFinder.findFile(environment.getDirectory(), filepattern) != null) {
                return new PassedBomToolResult();
            }
        }
        return new FilesNotFoundBomToolResult(SUPPORTED_PROJECT_PATTERNS);
    }

    @Override
    public BomToolResult extractable() throws BomToolException {
        inspectorExe = nugetInspectorManager.findNugetInspector();

        if (inspectorExe == null) {
            return new InspectorNotFoundBomToolResult("nuget");
        }

        return new PassedBomToolResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return nugetInspectorExtractor.extract(environment.getDirectory(), inspectorExe, extractionId);
    }

    @Override
    public String getName() {
        return "Project";
    }

    @Override
    public BomToolGroupType getBomToolGroupType() {
        return BomToolGroupType.NUGET;
    }

    @Override
    public BomToolType getBomToolType() {
        return BomToolType.NUGET_PROJECT_INSPECTOR;
    }
}
