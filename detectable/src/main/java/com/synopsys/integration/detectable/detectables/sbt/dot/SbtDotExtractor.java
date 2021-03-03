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
package com.synopsys.integration.detectable.detectables.sbt.dot;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.executable.Executable;
import com.synopsys.integration.executable.ExecutableOutput;

public class SbtDotExtractor {
    private final DetectableExecutableRunner executableRunner;
    private final SbtDotOutputParser sbtDotOutputParser;
    private final SbtProjectParser sbtProjectParser;
    private final DotGraphTransformer dotGraphTransformer;

    public SbtDotExtractor(DetectableExecutableRunner executableRunner, final SbtDotOutputParser sbtDotOutputParser, final SbtProjectParser sbtProjectParser,
        final DotGraphTransformer dotGraphTransformer) {
        this.executableRunner = executableRunner;
        this.sbtDotOutputParser = sbtDotOutputParser;
        this.sbtProjectParser = sbtProjectParser;
        this.dotGraphTransformer = dotGraphTransformer;
    }

    public Extraction extract(File directory, ExecutableTarget sbt) throws DetectableException {
        try {
            Executable projectExecutable = ExecutableUtils.createFromTarget(directory, sbt, "\"print projectID\"");
            ExecutableOutput projectOutput = executableRunner.executeSuccessfully(projectExecutable);
            SbtProjects sbtProjects = sbtProjectParser.parseProjectIDOutput(projectOutput.getStandardOutputAsList());

            Executable dotExecutable = ExecutableUtils.createFromTarget(directory, sbt, "dependencyDot");
            ExecutableOutput dotOutput = executableRunner.executeSuccessfully(dotExecutable);
            List<File> dotGraphs = sbtDotOutputParser.parseGeneratedGraphFiles(dotOutput.getStandardOutputAsList());
            List<CodeLocation> codeLocations = dotGraphTransformer.createCodeLocations(sbtProjects.getAllProjects(), dotGraphs);

            dotGraphs.forEach(File::deleteOnExit);

            Extraction.Builder builder = new Extraction.Builder().success(codeLocations);
            if (sbtProjects.getRootProject() != null) {
                builder.projectName(sbtProjects.getRootProject().getName());
                builder.projectVersion(sbtProjects.getRootProject().getVersion());
            }
            return builder.build();
        } catch (ExecutableFailedException e) {
            return Extraction.fromFailedExecutable(e);
        } catch (IOException e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
