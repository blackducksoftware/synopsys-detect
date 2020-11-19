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
    public List<ElementParser> createParsers() {
        List<ElementParser> elementParsers = new ArrayList<>();
        ElementParser requiresElementParser = new ListElementParser(conanInfoLineAnalyzer, "Requires", (ConanNodeBuilder nodeBuilder, String listItem) -> nodeBuilder.addRequiresRef(listItem));
        elementParsers.add(requiresElementParser);
        ElementParser buildRequiresElementParser = new ListElementParser(conanInfoLineAnalyzer, "Build Requires", (ConanNodeBuilder nodeBuilder, String listItem) -> nodeBuilder.addBuildRequiresRef(listItem));
        elementParsers.add(buildRequiresElementParser);
        ElementParser requiredByElementParser = new ListElementParser(conanInfoLineAnalyzer, "Required By", (ConanNodeBuilder nodeBuilder, String listItem) -> nodeBuilder.addRequiredByRef(listItem));
        elementParsers.add(requiredByElementParser);
        ElementParser packageIdParser = new KeyValuePairElementParser(conanInfoLineAnalyzer, "ID", (ConanNodeBuilder nodeBuilder, String parsedValue) -> nodeBuilder.setPackageId(parsedValue));
        elementParsers.add(packageIdParser);
        ElementParser recipeRevisionParser = new KeyValuePairElementParser(conanInfoLineAnalyzer, "Revision", (ConanNodeBuilder nodeBuilder, String parsedValue) -> nodeBuilder.setRecipeRevision(parsedValue));
        elementParsers.add(recipeRevisionParser);
        ElementParser packageRevisionParser = new KeyValuePairElementParser(conanInfoLineAnalyzer, "Package revision", (ConanNodeBuilder nodeBuilder, String parsedValue) -> nodeBuilder.setPackageRevision(parsedValue));
        elementParsers.add(packageRevisionParser);
        return elementParsers;
    }
}
