package com.synopsys.integration.detectable.detectables.conan.cli.parser.element;

import java.util.List;

import com.synopsys.integration.detectable.detectables.conan.graph.ConanNodeBuilder;

public interface ElementTypeParser {
    boolean applies(String elementLine);

    int parseElement(ConanNodeBuilder<String> nodeBuilder, List<String> conanInfoOutputLines, int bodyElementLineIndex);
}
