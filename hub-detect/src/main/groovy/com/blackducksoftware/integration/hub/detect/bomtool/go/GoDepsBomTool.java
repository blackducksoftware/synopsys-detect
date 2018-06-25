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
package com.blackducksoftware.integration.hub.detect.bomtool.go;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.bomtool.go.extraction.GoDepsExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.result.BomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.FileNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.PassedBomToolResult;
import com.blackducksoftware.integration.hub.detect.evaluation.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction;
import com.blackducksoftware.integration.hub.detect.model.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

public class GoDepsBomTool extends BomTool {
    public static final String GODEPS_DIRECTORYNAME = "Godeps";

    private final DetectFileFinder fileFinder;
    private final GoDepsExtractor goDepsExtractor;

    private File goDepsDirectory;

    public GoDepsBomTool(final BomToolEnvironment environment, final DetectFileFinder fileFinder, final GoDepsExtractor goDepsExtractor) {
        super(environment, "Go Deps Lock File", BomToolGroupType.GO_GODEP, BomToolType.GO_DEPS);
        this.fileFinder = fileFinder;
        this.goDepsExtractor = goDepsExtractor;
    }

    @Override
    public BomToolResult applicable() {
        goDepsDirectory = fileFinder.findFile(environment.getDirectory(), GODEPS_DIRECTORYNAME);
        if (goDepsDirectory == null) {
            return new FileNotFoundBomToolResult(GODEPS_DIRECTORYNAME);
        }

        return new PassedBomToolResult();
    }

    @Override
    public BomToolResult extractable() {
        return new PassedBomToolResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return goDepsExtractor.extract(this.getBomToolType(), environment.getDirectory(), goDepsDirectory);
    }

}
