package com.synopsys.integration.detectable.detectables.yarn.parse.entry.element;

import java.util.List;
import java.util.StringTokenizer;
import java.util.function.BiConsumer;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryBuilder;

public class YarnLockKeyValuePairElementParser implements YarnLockElementTypeParser {
    private final YarnLockLineAnalyzer yarnLockLineAnalyzer;
    private final String targetKey;
    private final BiConsumer<YarnLockEntryBuilder, String> valueConsumer;

    public YarnLockKeyValuePairElementParser(YarnLockLineAnalyzer yarnLockLineAnalyzer, String targetKey, BiConsumer<YarnLockEntryBuilder, String> valueConsumer) {
        this.yarnLockLineAnalyzer = yarnLockLineAnalyzer;
        this.targetKey = targetKey;
        this.valueConsumer = valueConsumer;
    }

    @Override
    public boolean applies(String elementLine) {
        elementLine = elementLine.trim();
        if (!elementLine.contains(" ") && elementLine.endsWith(":")) {
            return false; // looks like a key: (followed by a list)
        }
        StringTokenizer tokenizer = yarnLockLineAnalyzer.createKeyValueTokenizer(elementLine);
        String parsedKey = tokenizer.nextToken();
        return targetKey.equalsIgnoreCase(parsedKey) && tokenizer.hasMoreTokens();
    }

    @Override
    public int parseElement(YarnLockEntryBuilder entryBuilder, List<String> yarnLockLines, int bodyElementLineIndex) {
        StringTokenizer tokenizer = yarnLockLineAnalyzer.createKeyValueTokenizer(yarnLockLines.get(bodyElementLineIndex));
        tokenizer.nextToken(); // skip over key
        String value = tokenizer.nextToken().trim();
        valueConsumer.accept(entryBuilder, value);
        return bodyElementLineIndex;
        // TODO: See YarnLockParser.parseVersionFromLine() and make this method equivalent
    }
}
