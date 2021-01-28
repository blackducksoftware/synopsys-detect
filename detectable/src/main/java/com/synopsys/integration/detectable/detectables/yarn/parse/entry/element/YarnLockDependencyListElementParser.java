package com.synopsys.integration.detectable.detectables.yarn.parse.entry.element;

import java.util.List;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryBuilder;

public class YarnLockDependencyListElementParser implements YarnLockElementTypeParser {
    private final YarnLockLineAnalyzer yarnLockLineAnalyzer;
    private final YarnLockDependencySpecParser yarnLockDependencySpecParser;

    public YarnLockDependencyListElementParser(YarnLockLineAnalyzer yarnLockLineAnalyzer, YarnLockDependencySpecParser yarnLockDependencySpecParser) {
        this.yarnLockLineAnalyzer = yarnLockLineAnalyzer;
        this.yarnLockDependencySpecParser = yarnLockDependencySpecParser;
    }

    @Override
    public boolean applies(String elementLine) {
        elementLine = elementLine.trim();
        if (!elementLine.contains(" ") && elementLine.endsWith(":")) {
            String listKey = elementLine.substring(0, elementLine.length() - 1);
            return "dependencies".equals(listKey);
        }
        return false;
    }

    @Override
    public int parseElement(YarnLockEntryBuilder entryBuilder, List<String> yarnLockLines, int bodyElementLineIndex) {
        for (int curLineIndex = bodyElementLineIndex + 1; curLineIndex < yarnLockLines.size(); curLineIndex++) {
            String line = yarnLockLines.get(curLineIndex);
            int depth = yarnLockLineAnalyzer.measureIndentDepth(line);
            if (depth != 2) {
                return curLineIndex - 1;
            }
            YarnLockDependency yarnLockDependency = yarnLockDependencySpecParser.parse(line.trim(), false);
            entryBuilder.addDependency(yarnLockDependency);
        }
        return yarnLockLines.size() - 1;
    }
}
