/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
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
