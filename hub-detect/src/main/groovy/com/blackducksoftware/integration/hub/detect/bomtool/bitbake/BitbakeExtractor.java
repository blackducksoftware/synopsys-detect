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
package com.blackducksoftware.integration.hub.detect.bomtool.bitbake;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.paypal.digraph.parser.GraphParser;
import com.synopsys.integration.hub.bdio.graph.DependencyGraph;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalId;

public class BitbakeExtractor {
    private static final String RECIPE_DEPENDS_FILE_NAME = "recipe-depends.dot";

    private final ExecutableManager executableManager;
    private final ExecutableRunner executableRunner;
    private final DetectConfiguration detectConfiguration;
    private final DirectoryManager directoryManager;
    private final DetectFileFinder detectFileFinder;
    private final GraphParserTransformer graphParserTransformer;

    public BitbakeExtractor(final ExecutableManager executableManager, final ExecutableRunner executableRunner, final DetectConfiguration detectConfiguration, final DirectoryManager directoryManager,
        final DetectFileFinder detectFileFinder, final GraphParserTransformer graphParserTransformer) {
        this.executableManager = executableManager;
        this.executableRunner = executableRunner;
        this.detectConfiguration = detectConfiguration;
        this.directoryManager = directoryManager;
        this.detectFileFinder = detectFileFinder;
        this.graphParserTransformer = graphParserTransformer;
    }

    public Extraction extract(final ExtractionId extractionId, final String foundBuildEnvScriptPath, final String sourcePath) throws ExecutableRunnerException, FileNotFoundException {
        final String packageName = detectConfiguration.getProperty(DetectProperty.DETECT_BITBAKE_PACKAGE_NAME, PropertyAuthority.None);
        final BitbakeResult bitbakeResult = executeBitbake(extractionId, foundBuildEnvScriptPath);
        final int returnCode = bitbakeResult.getExecutableOutput().getReturnCode();

        if (returnCode != 0) {
            final Extraction.Builder builder = new Extraction.Builder().failure(String.format("Executing command '%s' returned a non-zero exit code %s", bitbakeResult.getExecutableDescription(), returnCode));
            return builder.build();
        }

        final File recipeDependsFile = bitbakeResult.getRecipeDependsFile();
        if (recipeDependsFile == null) {
            final Extraction.Builder builder = new Extraction.Builder().failure(String.format("Could not find expected file: %s", RECIPE_DEPENDS_FILE_NAME));
            return builder.build();
        }

        final InputStream recipeDependsInputStream = new FileInputStream(recipeDependsFile);
        final GraphParser graphParser = new GraphParser(recipeDependsInputStream);
        final DependencyGraph dependencyGraph = graphParserTransformer.transform(graphParser);

        final ExternalId externalId = new ExternalId(BitbakeBomTool.YOCTO_FORGE);
        final DetectCodeLocation detectCodeLocation = new DetectCodeLocation.Builder(BomToolGroupType.BITBAKE, BomToolType.BITBAKE_CLI, sourcePath, externalId, dependencyGraph).build();
        final Extraction extraction = new Extraction.Builder()
                                          .projectName(packageName)
                                          .success(detectCodeLocation)
                                          .build();

        return extraction;
    }

    private BitbakeResult executeBitbake(final ExtractionId extractionId, final String foundBuildEnvScriptPath) throws ExecutableRunnerException {
        final File outputDirectory = directoryManager.getExtractionOutputDirectory(extractionId);
        final String packageName = detectConfiguration.getProperty(DetectProperty.DETECT_BITBAKE_PACKAGE_NAME, PropertyAuthority.None);
        final String bashExecutablePath = executableManager.getExecutablePathOrOverride(ExecutableType.BASH, true, "", detectConfiguration.getProperty(DetectProperty.DETECT_BASH_PATH, PropertyAuthority.None));

        final List<String> arguments = new ArrayList<>();
        arguments.add("-c");
        arguments.add(". " + foundBuildEnvScriptPath + "; bitbake -g " + packageName);
        final Executable sourceExecutable = new Executable(outputDirectory, bashExecutablePath, arguments);

        final ExecutableOutput executableOutput = executableRunner.execute(sourceExecutable);
        final String executableDescription = sourceExecutable.getExecutableDescription();
        final File bitbakeBuildDirectory = new File(outputDirectory, "build");
        final File recipeDependsFile = detectFileFinder.findFile(bitbakeBuildDirectory, RECIPE_DEPENDS_FILE_NAME);

        return new BitbakeResult(executableOutput, executableDescription, recipeDependsFile);
    }

}
