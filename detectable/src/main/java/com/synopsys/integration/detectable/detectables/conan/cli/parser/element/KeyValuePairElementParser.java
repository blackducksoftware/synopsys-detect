package com.synopsys.integration.detectable.detectables.conan.cli.parser.element;

import java.util.List;
import java.util.StringTokenizer;
import java.util.function.BiConsumer;

import com.synopsys.integration.detectable.detectables.conan.cli.parser.ConanInfoLineAnalyzer;
import com.synopsys.integration.detectable.detectables.conan.graph.ConanNodeBuilder;

public class KeyValuePairElementParser implements ElementTypeParser {
    private final ConanInfoLineAnalyzer conanInfoLineAnalyzer;
    private final String targetKey;
    private final BiConsumer<ConanNodeBuilder<String>, String> valueConsumer;

    public KeyValuePairElementParser(ConanInfoLineAnalyzer conanInfoLineAnalyzer, String targetKey, BiConsumer<ConanNodeBuilder<String>, String> valueConsumer) {
        this.conanInfoLineAnalyzer = conanInfoLineAnalyzer;
        this.targetKey = targetKey;
        this.valueConsumer = valueConsumer;
    }

    @Override
    public boolean applies(String elementLine) {
        StringTokenizer tokenizer = conanInfoLineAnalyzer.createTokenizer(elementLine);
        String parsedKey = tokenizer.nextToken();
        return targetKey.equalsIgnoreCase(parsedKey) && tokenizer.hasMoreTokens();
    }

    @Override
    public int parseElement(ConanNodeBuilder<String> nodeBuilder, List<String> conanInfoOutputLines, int bodyElementLineIndex) {
        StringTokenizer tokenizer = conanInfoLineAnalyzer.createTokenizer(conanInfoOutputLines.get(bodyElementLineIndex));
        tokenizer.nextToken(); // skip over key
        String value = tokenizer.nextToken().trim();
        valueConsumer.accept(nodeBuilder, value);
        return bodyElementLineIndex;
    }
}
