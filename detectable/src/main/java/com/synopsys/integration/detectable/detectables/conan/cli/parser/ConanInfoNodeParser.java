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
package com.synopsys.integration.detectable.detectables.conan.cli.parser;

import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.conan.cli.parser.element.ElementParser;
import com.synopsys.integration.detectable.detectables.conan.cli.parser.element.ListElementParser;
import com.synopsys.integration.detectable.detectables.conan.cli.parser.element.NameValuePairElementParser;
import com.synopsys.integration.detectable.detectables.conan.graph.ConanNodeBuilder;

public class ConanInfoNodeParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ConanInfoLineAnalyzer conanInfoLineAnalyzer;

    public ConanInfoNodeParser(ConanInfoLineAnalyzer conanInfoLineAnalyzer) {
        this.conanInfoLineAnalyzer = conanInfoLineAnalyzer;
    }

    /*
     * A node looks like this:
     * ref:
     *     node body element
     */
    public ConanInfoNodeParseResult parseNode(List<String> conanInfoOutputLines, int nodeStartIndex) {
        String nodeHeaderLine = conanInfoOutputLines.get(nodeStartIndex);
        ConanNodeBuilder nodeBuilder = new ConanNodeBuilder();
        nodeBuilder.setRef(nodeHeaderLine);
        int bodyLineCount = 0;
        for (int lineIndex = nodeStartIndex + 1; lineIndex < conanInfoOutputLines.size(); lineIndex++) {
            String nodeBodyLine = conanInfoOutputLines.get(lineIndex);
            logger.trace(String.format("Parsing line: %d: %s", lineIndex + 1, nodeBodyLine));
            // Check to see if we've overshot the end of the node
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
        int indentDepth = conanInfoLineAnalyzer.measureIndentDepth(nodeBodyLine);
        if (indentDepth > 0) {
            // We're not done parsing this node
            return Optional.empty();
        }
        if (bodyLineCount == 0) {
            logger.trace("This wasn't a node (it was just a conan info command log message)");
            return Optional.of(new ConanInfoNodeParseResult(nodeStartIndex));
        } else {
            logger.trace("Reached end of node");
            return Optional.of(new ConanInfoNodeParseResult(lineIndex - 1, nodeBuilder.build()));
        }
    }

    private int parseBodyElement(List<String> conanInfoOutputLines, int bodyElementLineIndex, ConanNodeBuilder nodeBuilder) {
        ElementParser requiresElementParser = new ListElementParser(conanInfoLineAnalyzer, "Requires", listItem -> nodeBuilder.addRequiresRef(listItem));
        ElementParser buildRequiresElementParser = new ListElementParser(conanInfoLineAnalyzer, "Build Requires", listItem -> nodeBuilder.addBuildRequiresRef(listItem));
        ElementParser requiredByElementParser = new ListElementParser(conanInfoLineAnalyzer, "Required By", listItem -> nodeBuilder.addRequiredByRef(listItem));

        ElementParser packageIdParser = new NameValuePairElementParser(conanInfoLineAnalyzer, "ID", parsedValue -> nodeBuilder.setPackageId(parsedValue));
        ElementParser recipeRevisionParser = new NameValuePairElementParser(conanInfoLineAnalyzer, "Revision", parsedValue -> nodeBuilder.setRecipeRevision(parsedValue));
        ElementParser packageRevisionParser = new NameValuePairElementParser(conanInfoLineAnalyzer, "Package revision", parsedValue -> nodeBuilder.setPackageRevision(parsedValue));

        String line = conanInfoOutputLines.get(bodyElementLineIndex);

        StringTokenizer tokenizer = new StringTokenizer(conanInfoOutputLines.get(bodyElementLineIndex).trim(), ":");
        String key = tokenizer.nextToken();
        int lastLineParsed = bodyElementLineIndex;
        // TODO to make this more extensible: have a list of parsers; give each a swing at it until one says it handled it
        if (requiresElementParser.applies(line)) {
            lastLineParsed = requiresElementParser.parseElement(conanInfoOutputLines, bodyElementLineIndex);
        } else if (buildRequiresElementParser.applies(line)) {
            lastLineParsed = buildRequiresElementParser.parseElement(conanInfoOutputLines, bodyElementLineIndex);
        } else if (requiredByElementParser.applies(line)) {
            lastLineParsed = requiredByElementParser.parseElement(conanInfoOutputLines, bodyElementLineIndex);
        } else if (packageIdParser.applies(conanInfoOutputLines.get(bodyElementLineIndex))) {
            lastLineParsed = packageIdParser.parseElement(conanInfoOutputLines, bodyElementLineIndex);
        } else if (recipeRevisionParser.applies(conanInfoOutputLines.get(bodyElementLineIndex))) {
            lastLineParsed = recipeRevisionParser.parseElement(conanInfoOutputLines, bodyElementLineIndex);
        } else if (packageRevisionParser.applies(conanInfoOutputLines.get(bodyElementLineIndex))) {
            lastLineParsed = packageRevisionParser.parseElement(conanInfoOutputLines, bodyElementLineIndex);
        }

        return lastLineParsed;
    }

    private int parseListSubElement(List<String> conanInfoOutputLines, int subElementLineIndex, Consumer<String> collector) {
        for (int i = subElementLineIndex + 1; i < conanInfoOutputLines.size(); i++) {
            String line = conanInfoOutputLines.get(i);
            int depth = conanInfoLineAnalyzer.measureIndentDepth(line);
            if (depth != 2) {
                return i - 1;
            }
            collector.accept(conanInfoOutputLines.get(i).trim());
        }
        return conanInfoOutputLines.size() - 1;
    }
}
