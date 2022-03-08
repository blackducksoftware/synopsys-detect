package com.synopsys.integration.detectable.detectables.yarn.parse.entry.section;

import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryBuilder;

public class YarnLockDependencyMetaListSectionParser implements YarnLockEntrySectionParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final YarnLockLineAnalyzer yarnLockLineAnalyzer;

    public YarnLockDependencyMetaListSectionParser(YarnLockLineAnalyzer yarnLockLineAnalyzer) {
        this.yarnLockLineAnalyzer = yarnLockLineAnalyzer;
    }

    @Override
    public boolean applies(String sectionFirstLine) {
        if (yarnLockLineAnalyzer.measureIndentDepth(sectionFirstLine) != 1) {
            return false;
        }
        sectionFirstLine = sectionFirstLine.trim();
        if (!sectionFirstLine.contains(" ") && sectionFirstLine.endsWith(":")) {
            String listKey = sectionFirstLine.substring(0, sectionFirstLine.length() - 1);
            return "dependenciesMeta".equals(listKey);
        }
        return false;
    }

    @Override
    public int parseSection(YarnLockEntryBuilder entryBuilder, List<String> yarnLockLines, int lineIndexOfStartOfSection) {
        String curDependencyName = null;
        for (int curLineIndex = lineIndexOfStartOfSection + 1; curLineIndex < yarnLockLines.size(); curLineIndex++) {
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
        StringTokenizer tokenizer = TokenizerFactory.createKeyValueTokenizer(line);
        String key = tokenizer.nextToken();
        if ("optional".equals(key) && tokenizer.hasMoreTokens()) {
            String value = tokenizer.nextToken();
            if ("true".equalsIgnoreCase(value)) {
                makeDependencyOptional(entryBuilder, curDependencyName);
            }
        }
    }

    private void makeDependencyOptional(YarnLockEntryBuilder entryBuilder, String curDependencyName) {
        YarnLockDependency origDependency = entryBuilder.getDependencies().get(curDependencyName);
        if (origDependency == null) {
            logger.warn("Found metadata indicating dependency {} is optional, but it's not in the dependency list", curDependencyName);
            return;
        }
        logger.trace("Marking dependency {}:{} optional", origDependency.getName(), origDependency.getVersion());
        entryBuilder.getDependencies().remove(curDependencyName);
        YarnLockDependency replacementDependency = new YarnLockDependency(origDependency.getName(), origDependency.getVersion(), true);
        entryBuilder.getDependencies().put(curDependencyName, replacementDependency);
    }

    private String parseMetaDependencyNameFromLine(String line) {
        return yarnLockLineAnalyzer.unquote(StringUtils.substringBefore(line.trim(), ":"));
    }
}
