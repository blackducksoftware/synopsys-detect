/*
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
package com.synopsys.integration.detectable.detectables.yarn.parse.entry.section;

import java.util.List;
import java.util.StringTokenizer;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryBuilder;

public class YarnLockKeyValuePairSectionParser implements YarnLockEntrySectionParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final YarnLockLineAnalyzer yarnLockLineAnalyzer;
    private final String targetKey;
    private final BiConsumer<YarnLockEntryBuilder, String> valueConsumer;

    public YarnLockKeyValuePairSectionParser(YarnLockLineAnalyzer yarnLockLineAnalyzer, String targetKey, BiConsumer<YarnLockEntryBuilder, String> valueConsumer) {
        this.yarnLockLineAnalyzer = yarnLockLineAnalyzer;
        this.targetKey = targetKey;
        this.valueConsumer = valueConsumer;
    }

    @Override
    public boolean applies(String sectionFirstLine) {
        if (yarnLockLineAnalyzer.measureIndentDepth(sectionFirstLine) != 1) {
            return false;
        }
        sectionFirstLine = sectionFirstLine.trim();
        if (!sectionFirstLine.contains(" ") && sectionFirstLine.endsWith(":")) {
            return false; // This is just a key:
        }
        StringTokenizer tokenizer = TokenizerFactory.createKeyValueTokenizer(sectionFirstLine);
        String parsedKey = tokenizer.nextToken();
        return targetKey.equalsIgnoreCase(parsedKey) && tokenizer.hasMoreTokens();
    }

    @Override
    public int parseSection(YarnLockEntryBuilder entryBuilder, List<String> yarnLockLines, int lineIndexOfStartOfSection) {
        StringTokenizer tokenizer = TokenizerFactory.createKeyValueTokenizer(yarnLockLines.get(lineIndexOfStartOfSection));
        tokenizer.nextToken(); // skip over key
        String value = tokenizer.nextToken().trim();
        value = yarnLockLineAnalyzer.unquote(value);
        logger.trace("\t{}: {}", targetKey, value);
        valueConsumer.accept(entryBuilder, value);
        return lineIndexOfStartOfSection;
    }
}
