package com.synopsys.integration.detectable.detectables.conan.cli.parser.element;

import java.util.List;
import java.util.StringTokenizer;
import java.util.function.BiConsumer;

import com.synopsys.integration.detectable.detectables.conan.cli.parser.ConanInfoLineAnalyzer;
import com.synopsys.integration.detectable.detectables.conan.graph.ConanNodeBuilder;

public class ListElementParser implements ElementTypeParser {
    private final ConanInfoLineAnalyzer conanInfoLineAnalyzer;
    private final String targetKey;
    private final BiConsumer<ConanNodeBuilder<String>, String> valueConsumer;

    public ListElementParser(ConanInfoLineAnalyzer conanInfoLineAnalyzer, String targetKey, BiConsumer<ConanNodeBuilder<String>, String> valueConsumer) {
        this.conanInfoLineAnalyzer = conanInfoLineAnalyzer;
        this.targetKey = targetKey;
        this.valueConsumer = valueConsumer;
    }

    @Override
    public boolean applies(String elementLine) {
        StringTokenizer tokenizer = conanInfoLineAnalyzer.createTokenizer(elementLine);
        String parsedKey = tokenizer.nextToken();
        return targetKey.equalsIgnoreCase(parsedKey);
    }

    @Override
    public int parseElement(ConanNodeBuilder<String> nodeBuilder, List<String> conanInfoOutputLines, int bodyElementLineIndex) {
        for (int curLineIndex = bodyElementLineIndex + 1; curLineIndex < conanInfoOutputLines.size(); curLineIndex++) {
            String line = conanInfoOutputLines.get(curLineIndex);
            int depth = conanInfoLineAnalyzer.measureIndentDepth(line);
            if (depth != 2) {
                return curLineIndex - 1;
            }
            valueConsumer.accept(nodeBuilder, line.trim());
        }
        return conanInfoOutputLines.size() - 1;
    }
}
