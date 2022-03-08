package com.synopsys.integration.detectable.detectables.yarn.parse.entry.section;

import java.util.List;
import java.util.Optional;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryBuilder;

public class YarnLockDependencyListSectionParser implements YarnLockEntrySectionParser {
    private final YarnLockLineAnalyzer yarnLockLineAnalyzer;
    private final YarnLockDependencySpecParser yarnLockDependencySpecParser;
    private final String targetListKey;
    private final boolean dependenciesAreOptional;

    public YarnLockDependencyListSectionParser(
        YarnLockLineAnalyzer yarnLockLineAnalyzer,
        YarnLockDependencySpecParser yarnLockDependencySpecParser,
        String targetListKey,
        boolean dependenciesAreOptional
    ) {
        this.yarnLockLineAnalyzer = yarnLockLineAnalyzer;
        this.yarnLockDependencySpecParser = yarnLockDependencySpecParser;
        this.targetListKey = targetListKey;
        this.dependenciesAreOptional = dependenciesAreOptional;
    }

    @Override
    public boolean applies(String sectionFirstLine) {
        if (yarnLockLineAnalyzer.measureIndentDepth(sectionFirstLine) != 1) {
            return false;
        }
        sectionFirstLine = sectionFirstLine.trim();
        if (!sectionFirstLine.contains(" ") && sectionFirstLine.endsWith(":")) {
            String listKey = sectionFirstLine.substring(0, sectionFirstLine.length() - 1);
            return listKey.equals(targetListKey);
        }
        return false;
    }

    @Override
    public int parseSection(YarnLockEntryBuilder entryBuilder, List<String> yarnLockLines, int lineIndexOfStartOfSection) {
        for (int curLineIndex = lineIndexOfStartOfSection + 1; curLineIndex < yarnLockLines.size(); curLineIndex++) {
            String line = yarnLockLines.get(curLineIndex);
            int depth = yarnLockLineAnalyzer.measureIndentDepth(line);
            if (depth != 2) {
                return curLineIndex - 1;
            }
            Optional<YarnLockDependency> yarnLockDependency = yarnLockDependencySpecParser.parse(line.trim(), dependenciesAreOptional);
            yarnLockDependency.ifPresent(entryBuilder::addDependency);
        }
        return yarnLockLines.size() - 1;
    }
}
