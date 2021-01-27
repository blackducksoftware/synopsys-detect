package com.synopsys.integration.detectable.detectables.yarn.parse.entry.element;

import java.util.List;
import java.util.function.BiConsumer;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryBuilder;

public class YarnLockListElementParser implements YarnLockElementTypeParser {
    private final YarnLockLineAnalyzer yarnLockLineAnalyzer;
    private final String targetKey;
    private final BiConsumer<YarnLockEntryBuilder, String> valueConsumer;

    public YarnLockListElementParser(YarnLockLineAnalyzer yarnLockLineAnalyzer, String targetKey, BiConsumer<YarnLockEntryBuilder, String> valueConsumer) {
        this.yarnLockLineAnalyzer = yarnLockLineAnalyzer;
        this.targetKey = targetKey;
        this.valueConsumer = valueConsumer;
    }

    @Override
    public boolean applies(String elementLine) {
        elementLine = elementLine.trim();
        if (!elementLine.contains(" ") && elementLine.endsWith(":")) {
            String listKey = elementLine.substring(0, elementLine.length() - 1);
            return targetKey.equals(listKey);
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
            valueConsumer.accept(entryBuilder, line.trim());
        }
        return yarnLockLines.size() - 1;
    }
}
