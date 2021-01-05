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
package com.synopsys.integration.detectable.detectables.conan.cli.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectables.conan.ConanCodeLocationGenerator;
import com.synopsys.integration.detectable.detectables.conan.ConanDetectableResult;
import com.synopsys.integration.detectable.detectables.conan.graph.ConanNode;

public class ConanInfoParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ConanInfoNodeParser conanInfoNodeParser;
    private final ConanCodeLocationGenerator conanCodeLocationGenerator;
    private final ExternalIdFactory externalIdFactory;

    public ConanInfoParser(ConanInfoNodeParser conanInfoNodeParser, ConanCodeLocationGenerator conanCodeLocationGenerator,
        ExternalIdFactory externalIdFactory) {
        this.conanInfoNodeParser = conanInfoNodeParser;
        this.conanCodeLocationGenerator = conanCodeLocationGenerator;
        this.externalIdFactory = externalIdFactory;
    }

    public ConanDetectableResult generateCodeLocationFromConanInfoOutput(String conanInfoOutput, boolean includeBuildDependencies, boolean preferLongFormExternalIds) throws DetectableException {
        Map<String, ConanNode<String>> nodeMap = generateNodeMap(conanInfoOutput);
        return conanCodeLocationGenerator.generateCodeLocationFromNodeMap(externalIdFactory,
            includeBuildDependencies, preferLongFormExternalIds, nodeMap);
    }

    /*
     * Conan info command output: some (irrelevant to us) log messages, followed by a list of nodes.
     * A node looks like this:
     * ref:
     *     key1: value
     *     key2:
     *         list of values
     *     ....
     */
    private Map<String, ConanNode<String>> generateNodeMap(String conanInfoOutput) {
        Map<String, ConanNode<String>> graphNodes = new HashMap<>();
        List<String> conanInfoOutputLines = Arrays.asList(conanInfoOutput.split("\n"));
        int lineIndex = 0;
        while (lineIndex < conanInfoOutputLines.size()) {
            String line = conanInfoOutputLines.get(lineIndex);
            logger.trace("Parsing line: {}: {}", lineIndex + 1, line);
            // Parse the entire node
            ConanInfoNodeParseResult nodeParseResult = conanInfoNodeParser.parseNode(conanInfoOutputLines, lineIndex);
            // Some lines that look like the start of nodes aren't actually the start of nodes, and don't result in a node
            nodeParseResult.getConanNode().ifPresent(node -> graphNodes.put(node.getRef(), node));
            lineIndex = nodeParseResult.getLastParsedLineIndex();
            lineIndex++;
        }
        logger.trace("Reached end of Conan info output");
        return graphNodes;
    }
}
