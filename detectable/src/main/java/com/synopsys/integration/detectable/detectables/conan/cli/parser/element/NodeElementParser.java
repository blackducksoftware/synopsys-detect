package com.synopsys.integration.detectable.detectables.conan.cli.parser.element;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.detectable.detectables.conan.cli.parser.ConanInfoLineAnalyzer;
import com.synopsys.integration.detectable.detectables.conan.graph.ConanNodeBuilder;

public class NodeElementParser {
    private final List<ElementTypeParser> elementParsers = new ArrayList<>();

    public NodeElementParser(ConanInfoLineAnalyzer conanInfoLineAnalyzer) {
        ElementTypeParser requiresElementParser = new ListElementParser(conanInfoLineAnalyzer, "Requires", (ConanNodeBuilder nodeBuilder, String listItem) -> nodeBuilder.addRequiresRef(listItem));
        elementParsers.add(requiresElementParser);
        ElementTypeParser buildRequiresElementParser = new ListElementParser(conanInfoLineAnalyzer, "Build Requires", (ConanNodeBuilder nodeBuilder, String listItem) -> nodeBuilder.addBuildRequiresRef(listItem));
        elementParsers.add(buildRequiresElementParser);
        ElementTypeParser requiredByElementParser = new ListElementParser(conanInfoLineAnalyzer, "Required By", (ConanNodeBuilder nodeBuilder, String listItem) -> nodeBuilder.addRequiredByRef(listItem));
        elementParsers.add(requiredByElementParser);
        ElementTypeParser packageIdParser = new KeyValuePairElementParser(conanInfoLineAnalyzer, "ID", (ConanNodeBuilder nodeBuilder, String parsedValue) -> nodeBuilder.setPackageId(parsedValue));
        elementParsers.add(packageIdParser);
        ElementTypeParser recipeRevisionParser = new KeyValuePairElementParser(conanInfoLineAnalyzer, "Revision", (ConanNodeBuilder nodeBuilder, String parsedValue) -> nodeBuilder.setRecipeRevision(parsedValue));
        elementParsers.add(recipeRevisionParser);
        ElementTypeParser packageRevisionParser = new KeyValuePairElementParser(conanInfoLineAnalyzer, "Package revision", (ConanNodeBuilder nodeBuilder, String parsedValue) -> nodeBuilder.setPackageRevision(parsedValue));
        elementParsers.add(packageRevisionParser);
    }

    @NotNull
    public int parseElement(ConanNodeBuilder nodeBuilder, List<String> conanInfoOutputLines, int bodyElementLineIndex) {
        String line = conanInfoOutputLines.get(bodyElementLineIndex);
        int lastLineParsed = elementParsers.stream()
                                 .filter(ep -> ep.applies(line))
                                 .findFirst()
                                 .map(ep -> ep.parseElement(nodeBuilder, conanInfoOutputLines, bodyElementLineIndex))
                                 .orElse(bodyElementLineIndex);
        return lastLineParsed;
    }
}
