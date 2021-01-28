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
package com.synopsys.integration.detectable.detectables.yarn.parse.entry.element;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryBuilder;

public class YarnLockEntryElementParser {
    private final List<YarnLockElementTypeParser> elementParsers = new ArrayList<>();

    public YarnLockEntryElementParser(YarnLockLineAnalyzer yarnLockLineAnalyzer, YarnLockDependencySpecParser yarnLockDependencySpecParser) {
        createElementTypeParser(() -> new YarnLockEntryHeaderParser(yarnLockLineAnalyzer));
        createElementTypeParser(() -> new YarnLockDependencyListElementParser(yarnLockLineAnalyzer, yarnLockDependencySpecParser));
        createElementTypeParser(() -> new YarnLockDependencyMetaListElementParser(yarnLockLineAnalyzer, yarnLockDependencySpecParser));
        createElementTypeParser(() -> new YarnLockKeyValuePairElementParser(yarnLockLineAnalyzer, "version", YarnLockEntryBuilder::setVersion));
    }

    private void createElementTypeParser(Supplier<YarnLockElementTypeParser> elementSupplier) {
        elementParsers.add(elementSupplier.get());
    }

    public int parseElement(YarnLockEntryBuilder entryBuilder, List<String> conanInfoOutputLines, int bodyElementLineIndex) {
        String line = conanInfoOutputLines.get(bodyElementLineIndex);
        if (line.startsWith("#") || line.trim().isEmpty()) {
            return bodyElementLineIndex;
        }
        return elementParsers.stream()
                   .filter(ep -> ep.applies(line))
                   .findFirst()
                   .map(ep -> ep.parseElement(entryBuilder, conanInfoOutputLines, bodyElementLineIndex))
                   .orElse(bodyElementLineIndex);
    }
}
