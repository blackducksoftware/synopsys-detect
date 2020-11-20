package com.synopsys.integration.detectable.detectables.conan.cli.parser.element;

import java.util.List;
import java.util.StringTokenizer;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.conan.cli.parser.ConanInfoLineAnalyzer;
import com.synopsys.integration.detectable.detectables.conan.graph.ConanNodeBuilder;

public class KeyValuePairElementParser implements ElementTypeParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ConanInfoLineAnalyzer conanInfoLineAnalyzer;
    private final String targetKey;
    private final BiConsumer<ConanNodeBuilder, String> valueConsumer;

    public KeyValuePairElementParser(ConanInfoLineAnalyzer conanInfoLineAnalyzer, String targetKey, BiConsumer<ConanNodeBuilder, String> valueConsumer) {
        this.conanInfoLineAnalyzer = conanInfoLineAnalyzer;
        this.targetKey = targetKey;
        this.valueConsumer = valueConsumer;
    }

    @Override
    public boolean applies(String elementLine) {
        StringTokenizer tokenizer = conanInfoLineAnalyzer.createTokenizer(elementLine);
        String parsedKey = tokenizer.nextToken();
        if (targetKey.equalsIgnoreCase(parsedKey) && tokenizer.hasMoreTokens()) {
            return true;
        }
        return false;
    }

    @Override
    public int parseElement(ConanNodeBuilder nodeBuilder, List<String> conanInfoOutputLines, int bodyElementLineIndex) {
        StringTokenizer tokenizer = conanInfoLineAnalyzer.createTokenizer(conanInfoOutputLines.get(bodyElementLineIndex));
        tokenizer.nextToken(); // skip over key
        String value = tokenizer.nextToken().trim();
        logger.trace(String.format("Parsed %s: %s", targetKey, value));
        valueConsumer.accept(nodeBuilder, value);
        return bodyElementLineIndex;
    }
}
