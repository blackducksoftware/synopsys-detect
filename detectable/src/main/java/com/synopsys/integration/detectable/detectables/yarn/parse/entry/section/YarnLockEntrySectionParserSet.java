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
package com.synopsys.integration.detectable.detectables.yarn.parse.entry.section;

import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryBuilder;

public class YarnLockEntrySectionParserSet {
    private final List<YarnLockEntrySectionParser> yarnLockEntrySectionParsers = new ArrayList<>();

    public YarnLockEntrySectionParserSet(YarnLockLineAnalyzer yarnLockLineAnalyzer, YarnLockDependencySpecParser yarnLockDependencySpecParser) {
        yarnLockEntrySectionParsers.add(new YarnLockHeaderSectionParser(yarnLockLineAnalyzer));
        yarnLockEntrySectionParsers.add(new YarnLockDependencyListSectionParser(yarnLockLineAnalyzer, yarnLockDependencySpecParser, "dependencies", false));
        yarnLockEntrySectionParsers.add(new YarnLockDependencyListSectionParser(yarnLockLineAnalyzer, yarnLockDependencySpecParser, "optionalDependencies", true));
        yarnLockEntrySectionParsers.add(new YarnLockDependencyMetaListSectionParser(yarnLockLineAnalyzer));
        yarnLockEntrySectionParsers.add(new YarnLockKeyValuePairSectionParser(yarnLockLineAnalyzer, "version", YarnLockEntryBuilder::setVersion));
    }

    public int parseSection(YarnLockEntryBuilder entryBuilder, List<String> yarnLockLines, int lineIndexOfStartOfSection) {
        String line = yarnLockLines.get(lineIndexOfStartOfSection);
        if (line.startsWith("#") || line.trim().isEmpty()) {
            return lineIndexOfStartOfSection;
        }
        return yarnLockEntrySectionParsers.stream()
                   .filter(ep -> ep.applies(line))
                   .findFirst()
                   .map(ep -> ep.parseSection(entryBuilder, yarnLockLines, lineIndexOfStartOfSection))
                   .orElse(lineIndexOfStartOfSection);
    }
}
