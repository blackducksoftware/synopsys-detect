/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.conan.cli;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.conan.ConanCodeLocationGenerator;
import com.synopsys.integration.detectable.detectables.conan.ConanDetectableResult;
import com.synopsys.integration.detectable.detectables.conan.graph.ConanNode;
import com.synopsys.integration.exception.IntegrationException;

public class ConanInfoParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ConanInfoNodeParser conanInfoNodeParser;
    private final ConanCodeLocationGenerator conanCodeLocationGenerator;

    public ConanInfoParser(ConanInfoNodeParser conanInfoNodeParser, ConanCodeLocationGenerator conanCodeLocationGenerator) {
        this.conanInfoNodeParser = conanInfoNodeParser;
        this.conanCodeLocationGenerator = conanCodeLocationGenerator;
    }

    public ConanDetectableResult generateCodeLocationFromConanInfoOutput(String conanInfoOutput, boolean includeBuildDependencies) throws IntegrationException {
        Map<String, ConanNode> nodeMap = generateNodeMap(conanInfoOutput);
        // Everything from here on should be usable by the future lockfile detectable
        ConanDetectableResult result = conanCodeLocationGenerator.generateCodeLocationFromNodeMap(includeBuildDependencies, nodeMap);
        return result;
    }

    private Map<String, ConanNode> generateNodeMap(String conanInfoOutput) {
        Map<String, ConanNode> graphNodes = new HashMap<>();
        List<String> conanInfoOutputLines = Arrays.asList(conanInfoOutput.split("\n"));
        int lineIndex = 0;
        while (lineIndex < conanInfoOutputLines.size()) {
            String line = conanInfoOutputLines.get(lineIndex);
            logger.trace(String.format("Parsing line: %d: %s", lineIndex + 1, line));
            ConanInfoNodeParseResult nodeParseResult = conanInfoNodeParser.parseNode(conanInfoOutputLines, lineIndex);
            if (nodeParseResult.getConanNode().isPresent()) {
                graphNodes.put(nodeParseResult.getConanNode().get().getRef(), nodeParseResult.getConanNode().get());
            }
            lineIndex = nodeParseResult.getLastParsedLineIndex();
            lineIndex++;
        }
        System.out.printf("Reached end of Conan info output\n");
        return graphNodes;
    }
}
