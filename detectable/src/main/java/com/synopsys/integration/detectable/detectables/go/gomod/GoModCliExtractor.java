/**
 * detectable
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
package com.synopsys.integration.detectable.detectables.go.gomod;

import java.io.File;
import java.util.List;
import java.util.Set;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.extraction.Extraction;

public class GoModCliExtractor {
    private final GoModCommandExecutor goModCommandExecutor;
    private final GoModGraphTransformer goModGraphTransformer;
    private final GoModGraphParser goModGraphParser;
    private final GoModWhyParser goModWhyParser;

    public GoModCliExtractor(GoModCommandExecutor executor, GoModGraphParser goModGraphParser, GoModGraphTransformer goModGraphTransformer, GoModWhyParser goModWhyParser) {
        this.goModGraphParser = goModGraphParser;
        this.goModWhyParser = goModWhyParser;
        this.goModCommandExecutor = executor;
        this.goModGraphTransformer = goModGraphTransformer;
    }

    public Extraction extract(File directory, ExecutableTarget goExe) {
        try {
            List<String> listOutput = goModCommandExecutor.generateGoListOutput(directory, goExe);
            List<String> listUJsonOutput = goModCommandExecutor.generateGoListUJsonOutput(directory, goExe);
            List<String> modGraphOutput = goModCommandExecutor.generateGoModGraphOutput(directory, goExe);
            List<String> modWhyOutput = goModCommandExecutor.generateGoModWhyOutput(directory, goExe);
            Set<String> moduleExclusionList = goModWhyParser.createModuleExclusionList(modWhyOutput);
            List<String> finalModGraphOutput = goModGraphTransformer.transformGoModGraphOutput(modGraphOutput, listUJsonOutput);
            List<CodeLocation> codeLocations = goModGraphParser.parseListAndGoModGraph(listOutput, finalModGraphOutput, moduleExclusionList);
            return new Extraction.Builder().success(codeLocations).build();//no project info - hoping git can help with that.
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
