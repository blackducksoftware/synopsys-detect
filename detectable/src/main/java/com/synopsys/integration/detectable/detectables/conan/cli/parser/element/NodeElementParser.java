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
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.detectable.detectables.conan.cli.parser.ConanInfoLineAnalyzer;
import com.synopsys.integration.detectable.detectables.conan.graph.GenericNodeBuilder;

public class NodeElementParser {
    private final List<ElementTypeParser> elementParsers = new ArrayList<>();

    public NodeElementParser(ConanInfoLineAnalyzer conanInfoLineAnalyzer) {
        createElementTypeParser(() -> new ListElementParser(conanInfoLineAnalyzer, "Requires", (GenericNodeBuilder<String> nodeBuilder, String listItem) -> nodeBuilder.addRequiresRef(listItem)));
        createElementTypeParser(() -> new ListElementParser(conanInfoLineAnalyzer, "Build Requires", (GenericNodeBuilder<String> nodeBuilder, String listItem) -> nodeBuilder.addBuildRequiresRef(listItem)));
        createElementTypeParser(() -> new KeyValuePairElementParser(conanInfoLineAnalyzer, "ID", (GenericNodeBuilder<String> nodeBuilder, String parsedValue) -> nodeBuilder.setPackageId(parsedValue)));
        createElementTypeParser(() -> new KeyValuePairElementParser(conanInfoLineAnalyzer, "Revision", (GenericNodeBuilder<String> nodeBuilder, String parsedValue) -> nodeBuilder.setRecipeRevision(parsedValue)));
        createElementTypeParser(() -> new KeyValuePairElementParser(conanInfoLineAnalyzer, "Package revision", (GenericNodeBuilder<String> nodeBuilder, String parsedValue) -> nodeBuilder.setPackageRevision(parsedValue)));
    }

    private void createElementTypeParser(Supplier<ElementTypeParser> elementSupplier) {
        elementParsers.add(elementSupplier.get());
    }

    @NotNull
    public int parseElement(GenericNodeBuilder<String> nodeBuilder, List<String> conanInfoOutputLines, int bodyElementLineIndex) {
        String line = conanInfoOutputLines.get(bodyElementLineIndex);
        return elementParsers.stream()
                   .filter(ep -> ep.applies(line))
                   .findFirst()
                   .map(ep -> ep.parseElement(nodeBuilder, conanInfoOutputLines, bodyElementLineIndex))
                   .orElse(bodyElementLineIndex);
    }
}
