/**
 * detectable
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.yarn.parse;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.util.NameVersion;

public class YarnListParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final YarnLineLevelParser lineLevelParser;

    public YarnListParser(final YarnLineLevelParser lineLevelParser) {
        this.lineLevelParser = lineLevelParser;
    }

    public List<YarnListNode> parseYarnList(final List<String> yarnListAsList) {
        List<YarnListNode> yarnListNodes = new ArrayList<>();

        for (final String line : yarnListAsList) {
            if (shouldParseLine(line.toLowerCase().trim())){
                final String cleanedLine = lineLevelParser.replaceTreeCharactersWithSpaces(line);
                YarnListNode yarnListNode = parseDependencyFromLine(cleanedLine);
                yarnListNodes.add(yarnListNode);
            }
        }

        return yarnListNodes;
    }

    private boolean shouldParseLine(String line) {
        if (!line.contains("@") || line.startsWith("yarn list") || line.startsWith("done in") || line.startsWith("warning")) {
            return false;
        }
        return true;
    }

    public YarnListNode parseDependencyFromLine(final String cleanedLine) {
        final String fuzzyNameVersion = cleanedLine.trim();
        final NameVersion nameVersion = parseNameVersion(fuzzyNameVersion);

        final int lineLevel = lineLevelParser.parseTreeLevel(cleanedLine);

        return new YarnListNode(lineLevel, fuzzyNameVersion, nameVersion.getName(), nameVersion.getVersion());
    }

    public NameVersion parseNameVersion(String nameVersionLine) {
        String cleanedFuzzyNameVersionString = nameVersionLine;
        if (nameVersionLine.startsWith("@")) {
            cleanedFuzzyNameVersionString = nameVersionLine.substring(1);
        }

        final String[] nameVersionArray = cleanedFuzzyNameVersionString.split("@");
        return new NameVersion(nameVersionArray[0], nameVersionArray[1]);
    }
}
