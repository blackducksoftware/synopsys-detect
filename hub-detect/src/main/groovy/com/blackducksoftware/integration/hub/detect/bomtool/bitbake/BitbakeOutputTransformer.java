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
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.paypal.digraph.parser.GraphParser;
import com.synopsys.integration.hub.bdio.graph.DependencyGraph;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalId;

public class BitbakeOutputTransformer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final GraphParserTransformer graphParserTransformer;

    public BitbakeOutputTransformer(final GraphParserTransformer graphParserTransformer) {
        this.graphParserTransformer = graphParserTransformer;
    }

    public Optional<DetectCodeLocation> transformBitbakeOutput(final BitbakeOutput bitbakeOutput, final String sourcePath) {
        Optional<DetectCodeLocation> detectCodeLocation = Optional.empty();

        final File recipeDependsFile = bitbakeOutput.getRecipeDependsFile();
        final int returnCode = bitbakeOutput.getExecutableOutput().getReturnCode();

        if (returnCode != 0) {
            logger.error(String.format("Executing command '%s' returned a non-zero exit code %s", bitbakeOutput.getExecutableDescription(), returnCode));
        } else if (recipeDependsFile == null) {
            logger.error(String.format("Could not find expected file: %s", BitbakeExtractor.RECIPE_DEPENDS_FILE_NAME));
        } else {
            try {
                final InputStream recipeDependsInputStream = new FileInputStream(recipeDependsFile);
                final GraphParser graphParser = new GraphParser(recipeDependsInputStream);
                final DependencyGraph dependencyGraph = graphParserTransformer.transform(graphParser);
                final ExternalId externalId = new ExternalId(BitbakeBomTool.YOCTO_FORGE);
                final DetectCodeLocation codeLocation = new DetectCodeLocation.Builder(BomToolGroupType.BITBAKE, BomToolType.BITBAKE_CLI, sourcePath, externalId, dependencyGraph).build();

                detectCodeLocation = Optional.of(codeLocation);
            } catch (final FileNotFoundException e) {
                logger.error(String.format("File not found: %s", recipeDependsFile.getAbsolutePath()));
                logger.debug(e.getMessage(), e);
            }
        }

        return detectCodeLocation;
    }
}
