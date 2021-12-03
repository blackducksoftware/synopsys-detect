package com.synopsys.integration.detectable.detectables.conan.cli.parser.element;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.synopsys.integration.detectable.detectables.conan.cli.parser.ConanInfoLineAnalyzer;
import com.synopsys.integration.detectable.detectables.conan.graph.ConanNodeBuilder;

public class NodeElementParser {
    private final List<ElementTypeParser> elementParsers = new ArrayList<>();

    public NodeElementParser(ConanInfoLineAnalyzer conanInfoLineAnalyzer) {
        createElementTypeParser(() -> new ListElementParser(conanInfoLineAnalyzer, "Requires", ConanNodeBuilder::addRequiresRef));
        createElementTypeParser(() -> new ListElementParser(conanInfoLineAnalyzer, "Build Requires", ConanNodeBuilder::addBuildRequiresRef));
        createElementTypeParser(() -> new KeyValuePairElementParser(conanInfoLineAnalyzer, "ID", ConanNodeBuilder::setPackageId));
        createElementTypeParser(() -> new KeyValuePairElementParser(conanInfoLineAnalyzer, "Revision", ConanNodeBuilder::setRecipeRevision));
        createElementTypeParser(() -> new KeyValuePairElementParser(conanInfoLineAnalyzer, "Package revision", ConanNodeBuilder::setPackageRevision));
    }

    private void createElementTypeParser(Supplier<ElementTypeParser> elementSupplier) {
        elementParsers.add(elementSupplier.get());
    }

    public int parseElement(ConanNodeBuilder<String> nodeBuilder, List<String> conanInfoOutputLines, int bodyElementLineIndex) {
        String line = conanInfoOutputLines.get(bodyElementLineIndex);
        return elementParsers.stream()
            .filter(ep -> ep.applies(line))
            .findFirst()
            .map(ep -> ep.parseElement(nodeBuilder, conanInfoOutputLines, bodyElementLineIndex))
            .orElse(bodyElementLineIndex);
    }
}
