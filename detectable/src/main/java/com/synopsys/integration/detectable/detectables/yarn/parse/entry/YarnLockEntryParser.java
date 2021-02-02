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
package com.synopsys.integration.detectable.detectables.yarn.parse.entry;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.element.YarnLockElementParser;

public class YarnLockEntryParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final YarnLockLineAnalyzer yarnLockLineAnalyzer;
    private final YarnLockElementParser yarnLockEntryElementParser;

    public YarnLockEntryParser(YarnLockLineAnalyzer yarnLockLineAnalyzer, YarnLockElementParser yarnLockEntryElementParser) {
        this.yarnLockLineAnalyzer = yarnLockLineAnalyzer;
        this.yarnLockEntryElementParser = yarnLockEntryElementParser;
    }

    public YarnLockEntryParseResult parseEntry(List<String> yarnLockFileLines, int nodeStartIndex) {
        YarnLockEntryBuilder yarnLockEntryBuilder = new YarnLockEntryBuilder();
        int entryLineIndex = 0;
        for (int fileLineIndex = nodeStartIndex; fileLineIndex < yarnLockFileLines.size(); fileLineIndex++) {
            String nodeBodyLine = yarnLockFileLines.get(fileLineIndex);
            // Check to see if we've overshot the end of the node
            Optional<YarnLockEntryParseResult> result = getResultIfDone(entryLineIndex, nodeBodyLine, fileLineIndex, nodeStartIndex, entryLineIndex, yarnLockEntryBuilder);
            if (result.isPresent()) {
                return result.get();
            }
            // parseElement tells this code what line to parse next (= where it left off)
            fileLineIndex = yarnLockEntryElementParser.parseElement(yarnLockEntryBuilder, yarnLockFileLines, fileLineIndex);
            entryLineIndex++;
        }
        Optional<YarnLockEntry> entry = yarnLockEntryBuilder.build();
        return new YarnLockEntryParseResult(yarnLockFileLines.size() - 1, entry.orElse(null));
    }

    private Optional<YarnLockEntryParseResult> getResultIfDone(int entryLineIndex, String nodeBodyLine, int lineIndex, int entryStartIndex, int bodyLineCount, YarnLockEntryBuilder entryBuilder) {
        if (entryLineIndex == 0) {
            // we're still on the first line, so can't be done yet
            return Optional.empty();
        }
        int indentDepth = yarnLockLineAnalyzer.measureIndentDepth(nodeBodyLine);
        if (indentDepth > 0) {
            // We're not done parsing this node
            return Optional.empty();
        }
        if (bodyLineCount == 0) {
            return Optional.of(new YarnLockEntryParseResult(entryStartIndex));
        } else {
            Optional<YarnLockEntry> node = entryBuilder.build();
            return Optional.of(new YarnLockEntryParseResult(lineIndex - 1, node.orElse(null)));
        }
    }
}
