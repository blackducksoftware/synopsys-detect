package com.synopsys.integration.detectable.detectables.conan.cli.parser.element;

import java.util.List;
import java.util.StringTokenizer;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.detectable.detectables.conan.graph.ConanNodeBuilder;

public class PackageIdElementParser implements ElementParser {

    @Override
    public boolean applies(String elementLine) {
        StringTokenizer tokenizer = createTokenizer(elementLine);
        String key = tokenizer.nextToken();
        if ("ID".equalsIgnoreCase(key) && tokenizer.hasMoreTokens()) {
            return true;
        }
        return false;
    }

    @Override
    public int parseElement(List<String> conanInfoOutputLines, int bodyElementLineIndex, ConanNodeBuilder nodeBuilder) {
        StringTokenizer tokenizer = createTokenizer(conanInfoOutputLines.get(bodyElementLineIndex));
        String key = tokenizer.nextToken();

        return bodyElementLineIndex;
    }

    @NotNull
    private StringTokenizer createTokenizer(String line) {
        return new StringTokenizer(line.trim(), ":");
    }
}
