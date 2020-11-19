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

import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.conan.graph.ConanNodeBuilder;

public class ConanInfoNodeParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ConanInfoNodeParseResult parseNode(List<String> conanInfoOutputLines, int nodeStartIndex) {
        String nodeHeaderLine = conanInfoOutputLines.get(nodeStartIndex);
        ConanNodeBuilder nodeBuilder = new ConanNodeBuilder();
        nodeBuilder.setRef(nodeHeaderLine);
        int bodyLineCount = 0;
        for (int lineIndex = nodeStartIndex + 1; lineIndex < conanInfoOutputLines.size(); lineIndex++) {
            String nodeBodyLine = conanInfoOutputLines.get(lineIndex);
            logger.trace(String.format("Parsing line: %d: %s", lineIndex + 1, nodeBodyLine));
            Optional<ConanInfoNodeParseResult> result = getResultIfDone(nodeBodyLine, lineIndex, nodeStartIndex, bodyLineCount, nodeBuilder);
            if (result.isPresent()) {
                return result.get();
            }
            bodyLineCount++;
            lineIndex = parseBodyElement(conanInfoOutputLines, lineIndex, nodeBuilder);
        }
        logger.trace("Reached end of conan info output");
        return new ConanInfoNodeParseResult(conanInfoOutputLines.size() - 1, nodeBuilder.build());
    }

    private Optional<ConanInfoNodeParseResult> getResultIfDone(String nodeBodyLine, int lineIndex, int nodeStartIndex, int bodyLineCount, ConanNodeBuilder nodeBuilder) {
        int indentDepth = measureIndentDepth(nodeBodyLine);
        if (indentDepth > 0) {
            // Not done parsing node
            return Optional.empty();
        }
        if (bodyLineCount == 0) {
            logger.trace("This wasn't a node");
            return Optional.of(new ConanInfoNodeParseResult(nodeStartIndex));
        } else {
            logger.trace("Reached end of node");
            return Optional.of(new ConanInfoNodeParseResult(lineIndex - 1, nodeBuilder.build()));
        }
    }

    private int parseBodyElement(List<String> conanInfoOutputLines, int bodyElementLineIndex, ConanNodeBuilder nodeBuilder) {
        StringTokenizer stringTokenizer = new StringTokenizer(conanInfoOutputLines.get(bodyElementLineIndex).trim(), ":");
        String key = stringTokenizer.nextToken();
        int lastLineParsed = bodyElementLineIndex;
        if ("Requires".equals(key)) {
            lastLineParsed = parseListSubElement(conanInfoOutputLines, bodyElementLineIndex, ref -> nodeBuilder.addRequiresRef(ref));
        } else if ("Build Requires".equals(key)) {
            lastLineParsed = parseListSubElement(conanInfoOutputLines, bodyElementLineIndex, ref -> nodeBuilder.addBuildRequiresRef(ref));
        } else if ("Required By".equals(key)) {
            lastLineParsed = parseListSubElement(conanInfoOutputLines, bodyElementLineIndex, ref -> nodeBuilder.addRequiredByRef(ref));
        } else if (stringTokenizer.hasMoreTokens()) {
            String value = stringTokenizer.nextToken().trim();
            if ("ID".equals(key)) {
                logger.trace(String.format("Found Package ID: %s", value));
                nodeBuilder.setPackageId(value);
            } else if ("Revision".equals(key)) {
                logger.trace(String.format("Found Recipe Revision: %s", value));
                nodeBuilder.setRecipeRevision(value);
            } else if ("Package revision".equals(key)) {
                logger.trace(String.format("Found Package Revision: %s", value));
                nodeBuilder.setPackageRevision(value);
            }
        }
        return lastLineParsed;
    }

    private int parseListSubElement(List<String> conanInfoOutputLines, int subElementLineIndex, Consumer<String> collector) {
        for (int i = subElementLineIndex + 1; i < conanInfoOutputLines.size(); i++) {
            String line = conanInfoOutputLines.get(i);
            int depth = measureIndentDepth(line);
            if (depth != 2) {
                return i - 1;
            }
            collector.accept(conanInfoOutputLines.get(i).trim());
        }
        return conanInfoOutputLines.size() - 1;
    }

    private int measureIndentDepth(String line) {
        if (StringUtils.isBlank(line)) {
            return 0;
        }
        int leadingSpaceCount = countLeadingSpaces(line);
        if ((leadingSpaceCount % 4) != 0) {
            logger.warn(String.format("Leading space count for '%s' is %d; expected it to be divisible by 4",
                line, leadingSpaceCount));
        }
        return countLeadingSpaces(line) / 4;
    }

    private int countLeadingSpaces(String line) {
        int leadingSpaceCount = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ' ') {
                leadingSpaceCount++;
            } else if (line.charAt(i) == '\t') {
                leadingSpaceCount += 4;
            } else {
                break;
            }
        }
        return leadingSpaceCount;
    }
}
