package com.synopsys.integration.detectable.detectables.yarn.parse.entry.element;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryBuilder;

public class YarnLockDependencyMetaListElementParser implements YarnLockElementTypeParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final YarnLockLineAnalyzer yarnLockLineAnalyzer;
    private final YarnLockDependencySpecParser yarnLockDependencySpecParser;

    public YarnLockDependencyMetaListElementParser(YarnLockLineAnalyzer yarnLockLineAnalyzer, YarnLockDependencySpecParser yarnLockDependencySpecParser) {
        this.yarnLockLineAnalyzer = yarnLockLineAnalyzer;
        this.yarnLockDependencySpecParser = yarnLockDependencySpecParser;
    }

    @Override
    public boolean applies(String elementLine) {
        // TODO should test depth here and in others; maybe just ignore comments and empty lines first
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
                // TODO this is orig code; flexible enough? Use tokenizer?
                if (line.contains("optional: true")) {
                    markDependencyOptional(entryBuilder, curDependencyName);
                }
            }
        }
        return yarnLockLines.size() - 1;
    }

    private void markDependencyOptional(YarnLockEntryBuilder entryBuilder, String curDependencyName) {
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
