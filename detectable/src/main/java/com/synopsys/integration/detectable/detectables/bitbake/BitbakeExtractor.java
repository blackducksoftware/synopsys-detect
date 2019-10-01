/**
 * detectable
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.bitbake;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paypal.digraph.parser.GraphParser;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeFileType;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeGraph;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeRecipe;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeResult;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeGraphTransformer;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeLayersParser;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeRecipesParser;
import com.synopsys.integration.detectable.detectables.bitbake.parse.GraphParserTransformer;
import com.synopsys.integration.exception.IntegrationException;

public class BitbakeExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ExecutableRunner executableRunner;
    private final FileFinder fileFinder;
    private final GraphParserTransformer graphParserTransformer;
    private final BitbakeGraphTransformer bitbakeGraphTransformer;
    private final BitbakeLayersParser bitbakeLayersParser;
    private final BitbakeRecipesParser bitbakeRecipesParser;

    public BitbakeExtractor(final ExecutableRunner executableRunner, final FileFinder fileFinder, final GraphParserTransformer graphParserTransformer, final BitbakeGraphTransformer bitbakeGraphTransformer,
        final BitbakeLayersParser bitbakeLayersParser, final BitbakeRecipesParser bitbakeRecipesParser) {
        this.executableRunner = executableRunner;
        this.fileFinder = fileFinder;
        this.graphParserTransformer = graphParserTransformer;
        this.bitbakeGraphTransformer = bitbakeGraphTransformer;
        this.bitbakeLayersParser = bitbakeLayersParser;
        this.bitbakeRecipesParser = bitbakeRecipesParser;
    }

    public Extraction extract(final File sourceDirectory, final ExtractionEnvironment extractionEnvironment, final File buildEnvScript, final String[] sourceArguments, final String[] packageNames, final File bash) {
        final File outputDirectory = extractionEnvironment.getOutputDirectory();

        final List<CodeLocation> codeLocations = new ArrayList<>();
        final BitbakeSession bitbakeSession = new BitbakeSession(fileFinder, executableRunner, bitbakeLayersParser, bitbakeRecipesParser, outputDirectory, buildEnvScript, sourceArguments, bash);
        for (final String packageName : packageNames) {
            try {
                final BitbakeGraph bitbakeGraph = generateBitbakeGraph(bitbakeSession, sourceDirectory, packageName);
                final Map<String, Integer> layerPriorityMap = bitbakeSession.executeBitbakeForLayers();
                final Map<String, BitbakeRecipe> componentLayerMap = bitbakeSession.executeBitbakeForRecipeMap();

                final DependencyGraph dependencyGraph = bitbakeGraphTransformer.transform(bitbakeGraph, componentLayerMap, layerPriorityMap);
                final CodeLocation codeLocation = new CodeLocation(dependencyGraph);

                codeLocations.add(codeLocation);
            } catch (final IOException | IntegrationException | ExecutableRunnerException | NotImplementedException e) {
                logger.error(String.format("Failed to extract a Code Location while running Bitbake against package '%s'", packageName));
                logger.debug(e.getMessage(), e);
            }
        }

        final Extraction extraction;

        if (codeLocations.isEmpty()) {
            extraction = new Extraction.Builder()
                             .failure("No Code Locations were generated during extraction")
                             .build();
        } else {
            extraction = new Extraction.Builder()
                             .success(codeLocations)
                             .build();
        }

        return extraction;
    }

    private BitbakeGraph generateBitbakeGraph(final BitbakeSession bitbakeSession, final File sourceDirectory, final String packageName) throws ExecutableRunnerException, IOException, IntegrationException {
        final BitbakeResult bitbakeResult = bitbakeSession.executeBitbakeForDependencies(sourceDirectory, packageName).orElseThrow(() -> {
            final String filesSearchedFor = Arrays.stream(BitbakeFileType.values())
                                                .map(BitbakeFileType::getFileName)
                                                .collect(Collectors.joining(", "));
            return new IntegrationException(String.format("Failed to find any bitbake results. Looked for: %s", filesSearchedFor));
        });

        final File fileToParse = bitbakeResult.getFile();
        logger.trace(FileUtils.readFileToString(fileToParse, Charset.defaultCharset()));
        final InputStream dependsFileInputStream = FileUtils.openInputStream(fileToParse);
        final GraphParser graphParser = new GraphParser(dependsFileInputStream);

        final BitbakeFileType bitbakeFileType = bitbakeResult.getBitbakeFileType();
        return graphParserTransformer.transform(graphParser, bitbakeFileType);
    }
}
