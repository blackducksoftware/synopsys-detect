package com.synopsys.integration.detectable.detectables.conan.cli.parser.element;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.detectable.detectables.conan.cli.parser.ConanInfoLineAnalyzer;
import com.synopsys.integration.detectable.detectables.conan.graph.ConanNodeBuilder;

public class ElementParserFactory {
    private final ConanInfoLineAnalyzer conanInfoLineAnalyzer;

    public ElementParserFactory(ConanInfoLineAnalyzer conanInfoLineAnalyzer) {
        this.conanInfoLineAnalyzer = conanInfoLineAnalyzer;
    }

    @NotNull
    public List<ElementParser> createParsersForNode(ConanNodeBuilder nodeBuilder) {
        List<ElementParser> elementParsers = new ArrayList<>();
        ElementParser requiresElementParser = new ListElementParser(conanInfoLineAnalyzer, "Requires", listItem -> nodeBuilder.addRequiresRef(listItem));
        elementParsers.add(requiresElementParser);
        ElementParser buildRequiresElementParser = new ListElementParser(conanInfoLineAnalyzer, "Build Requires", listItem -> nodeBuilder.addBuildRequiresRef(listItem));
        elementParsers.add(buildRequiresElementParser);
        ElementParser requiredByElementParser = new ListElementParser(conanInfoLineAnalyzer, "Required By", listItem -> nodeBuilder.addRequiredByRef(listItem));
        elementParsers.add(requiredByElementParser);
        ElementParser packageIdParser = new KeyValuePairElementParser(conanInfoLineAnalyzer, "ID", parsedValue -> nodeBuilder.setPackageId(parsedValue));
        elementParsers.add(packageIdParser);
        ElementParser recipeRevisionParser = new KeyValuePairElementParser(conanInfoLineAnalyzer, "Revision", parsedValue -> nodeBuilder.setRecipeRevision(parsedValue));
        elementParsers.add(recipeRevisionParser);
        ElementParser packageRevisionParser = new KeyValuePairElementParser(conanInfoLineAnalyzer, "Package revision", parsedValue -> nodeBuilder.setPackageRevision(parsedValue));
        elementParsers.add(packageRevisionParser);
        return elementParsers;
    }
}
