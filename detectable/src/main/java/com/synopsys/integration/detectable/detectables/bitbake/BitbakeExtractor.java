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
package com.synopsys.integration.detectable.detectables.bitbake;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paypal.digraph.parser.GraphParser;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeGraph;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeRecipe;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeGraphTransformer;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeRecipesParser;
import com.synopsys.integration.detectable.detectables.bitbake.parse.GraphParserTransformer;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class BitbakeExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DetectableExecutableRunner executableRunner;
    private final FileFinder fileFinder;
    private final GraphParserTransformer graphParserTransformer;
    private final BitbakeGraphTransformer bitbakeGraphTransformer;
    private final BitbakeRecipesParser bitbakeRecipesParser;
    private final BitbakeRecipesToLayerMapConverter bitbakeRecipesToLayerMap;

    public BitbakeExtractor(DetectableExecutableRunner executableRunner, FileFinder fileFinder, GraphParserTransformer graphParserTransformer, BitbakeGraphTransformer bitbakeGraphTransformer,
        BitbakeRecipesParser bitbakeRecipesParser, BitbakeRecipesToLayerMapConverter bitbakeRecipesToLayerMap) {
        this.executableRunner = executableRunner;
        this.fileFinder = fileFinder;
        this.graphParserTransformer = graphParserTransformer;
        this.bitbakeGraphTransformer = bitbakeGraphTransformer;
        this.bitbakeRecipesParser = bitbakeRecipesParser;
        this.bitbakeRecipesToLayerMap = bitbakeRecipesToLayerMap;
    }

    public Extraction extract(File sourceDirectory, File buildEnvScript, List<String> sourceArguments, List<String> packageNames, Integer searchDepth, ExecutableTarget bash) {
        List<CodeLocation> codeLocations = new ArrayList<>();

        BitbakeSession bitbakeSession = new BitbakeSession(fileFinder, executableRunner, bitbakeRecipesParser, sourceDirectory, buildEnvScript, sourceArguments, bash);
        for (String packageName : packageNames) {
            try {
                BitbakeGraph bitbakeGraph = generateBitbakeGraph(bitbakeSession, sourceDirectory, packageName, searchDepth);
                List<BitbakeRecipe> bitbakeRecipes = bitbakeSession.executeBitbakeForRecipeLayerCatalog();
                Map<String, String> recipeNameToLayersMap = bitbakeRecipesToLayerMap.convert(bitbakeRecipes);

                DependencyGraph dependencyGraph = bitbakeGraphTransformer.transform(bitbakeGraph, recipeNameToLayersMap);
                CodeLocation codeLocation = new CodeLocation(dependencyGraph);

                codeLocations.add(codeLocation);

            } catch (IOException | IntegrationException | ExecutableRunnerException | NotImplementedException e) {
                logger.error(String.format("Failed to extract a Code Location while running Bitbake against package '%s'", packageName));
                logger.debug(e.getMessage(), e);
            }
        }

        Extraction extraction;

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

    private BitbakeGraph generateBitbakeGraph(BitbakeSession bitbakeSession, File sourceDirectory, String packageName, Integer searchDepth) throws ExecutableRunnerException, IOException, IntegrationException {
        File taskDependsFile = bitbakeSession.executeBitbakeForDependencies(sourceDirectory, packageName, searchDepth)
                                         .orElseThrow(() -> new IntegrationException("Failed to find file \"task-depends.dot\"."));

        logger.trace(FileUtils.readFileToString(taskDependsFile, Charset.defaultCharset()));

        InputStream dependsFileInputStream = FileUtils.openInputStream(taskDependsFile);
        GraphParser graphParser = new GraphParser(dependsFileInputStream);

        return graphParserTransformer.transform(graphParser);
    }
}
