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

import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryBuilder;

public class YarnLockDependencyMetaListElementParser implements YarnLockElementTypeParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final YarnLockLineAnalyzer yarnLockLineAnalyzer;

    public YarnLockDependencyMetaListElementParser(YarnLockLineAnalyzer yarnLockLineAnalyzer) {
        this.yarnLockLineAnalyzer = yarnLockLineAnalyzer;
    }

    @Override
    public boolean applies(String elementLine) {
        if (yarnLockLineAnalyzer.measureIndentDepth(elementLine) != 1) {
            return false;
        }
        elementLine = elementLine.trim();
        if (!elementLine.contains(" ") && elementLine.endsWith(":")) {
            String listKey = elementLine.substring(0, elementLine.length() - 1);
            return "dependenciesMeta".equals(listKey);
        }
        return false;
    }

    @Override
    public int parseElement(YarnLockEntryBuilder entryBuilder, List<String> yarnLockLines, int bodyElementLineIndex) {
        String curDependencyName = null;
        for (int curLineIndex = bodyElementLineIndex + 1; curLineIndex < yarnLockLines.size(); curLineIndex++) {
            String line = yarnLockLines.get(curLineIndex);
            int depth = yarnLockLineAnalyzer.measureIndentDepth(line);
            if (depth == 1) {
                return curLineIndex - 1;
            } else if (depth == 2) {
                curDependencyName = parseMetaDependencyNameFromLine(line);
            } else if (depth == 3) {
                makeOptionalIfOptional(entryBuilder, curDependencyName, line);
            }
        }
        return yarnLockLines.size() - 1;
    }

    private void makeOptionalIfOptional(YarnLockEntryBuilder entryBuilder, String curDependencyName, String line) {
        StringTokenizer tokenizer = yarnLockLineAnalyzer.createKeyValueTokenizer(line);
        String key = tokenizer.nextToken();
        if ("optional".equals(key)) {
            if (tokenizer.hasMoreTokens()) {
                String value = tokenizer.nextToken();
                if ("true".equalsIgnoreCase(value)) {
                    makeDependencyOptional(entryBuilder, curDependencyName);
                }
            }
        }
    }

    private void makeDependencyOptional(YarnLockEntryBuilder entryBuilder, String curDependencyName) {
        YarnLockDependency origDependency = entryBuilder.getDependencies().get(curDependencyName);
        if (origDependency == null) {
            logger.warn(String.format("Found metadata indicating dependency %s is optional, but it's not in the dependency list"));
            return;
        }
        entryBuilder.getDependencies().remove(curDependencyName);
        YarnLockDependency replacementDependency = new YarnLockDependency(origDependency.getName(),
            origDependency.getVersion(), true);
        entryBuilder.getDependencies().put(curDependencyName, replacementDependency);
    }

    private String parseMetaDependencyNameFromLine(String line) {
        return yarnLockLineAnalyzer.unquote(StringUtils.substringBefore(line.trim(), ":"));
    }
}
