package com.blackduck.integration.detectable.detectables.conan.cli.parser.conan1.element;

import java.util.List;

import com.blackduck.integration.detectable.detectables.conan.graph.ConanNodeBuilder;

public interface ElementTypeParser {
    boolean applies(String elementLine);

    int parseElement(ConanNodeBuilder<String> nodeBuilder, List<String> conanInfoOutputLines, int bodyElementLineIndex);
}
