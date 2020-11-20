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
package com.synopsys.integration.detectable.detectables.conan.cli.parser.element;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.detectable.detectables.conan.cli.parser.ConanInfoLineAnalyzer;
import com.synopsys.integration.detectable.detectables.conan.graph.ConanNodeBuilder;

public class NodeElementParser {
    private final List<ElementTypeParser> elementParsers = new ArrayList<>();

    public NodeElementParser(ConanInfoLineAnalyzer conanInfoLineAnalyzer) {
        ElementTypeParser requiresElementParser = new ListElementParser(conanInfoLineAnalyzer, "Requires", (ConanNodeBuilder nodeBuilder, String listItem) -> nodeBuilder.addRequiresRef(listItem));
        elementParsers.add(requiresElementParser);
        ElementTypeParser buildRequiresElementParser = new ListElementParser(conanInfoLineAnalyzer, "Build Requires", (ConanNodeBuilder nodeBuilder, String listItem) -> nodeBuilder.addBuildRequiresRef(listItem));
        elementParsers.add(buildRequiresElementParser);
        ElementTypeParser requiredByElementParser = new ListElementParser(conanInfoLineAnalyzer, "Required By", (ConanNodeBuilder nodeBuilder, String listItem) -> nodeBuilder.addRequiredByRef(listItem));
        elementParsers.add(requiredByElementParser);
        ElementTypeParser packageIdParser = new KeyValuePairElementParser(conanInfoLineAnalyzer, "ID", (ConanNodeBuilder nodeBuilder, String parsedValue) -> nodeBuilder.setPackageId(parsedValue));
        elementParsers.add(packageIdParser);
        ElementTypeParser recipeRevisionParser = new KeyValuePairElementParser(conanInfoLineAnalyzer, "Revision", (ConanNodeBuilder nodeBuilder, String parsedValue) -> nodeBuilder.setRecipeRevision(parsedValue));
        elementParsers.add(recipeRevisionParser);
        ElementTypeParser packageRevisionParser = new KeyValuePairElementParser(conanInfoLineAnalyzer, "Package revision", (ConanNodeBuilder nodeBuilder, String parsedValue) -> nodeBuilder.setPackageRevision(parsedValue));
        elementParsers.add(packageRevisionParser);
    }

    @NotNull
    public int parseElement(ConanNodeBuilder nodeBuilder, List<String> conanInfoOutputLines, int bodyElementLineIndex) {
        String line = conanInfoOutputLines.get(bodyElementLineIndex);
        int lastLineParsed = elementParsers.stream()
                                 .filter(ep -> ep.applies(line))
                                 .findFirst()
                                 .map(ep -> ep.parseElement(nodeBuilder, conanInfoOutputLines, bodyElementLineIndex))
                                 .orElse(bodyElementLineIndex);
        return lastLineParsed;
    }
}
