package com.synopsys.integration.detectable.detectables.yarn.parse.entry.element;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryBuilder;

public class YarnLockEntryElementParser {
    private final List<YarnLockElementTypeParser> elementParsers = new ArrayList<>();

    public YarnLockEntryElementParser(YarnLockLineAnalyzer yarnLockLineAnalyzer, YarnLockDependencySpecParser yarnLockDependencySpecParser) {
        // TODO
        // Instead of a generic ListElementParser, you need:
        // 1. DependencyListParser
        // 2. DependencyMetaListParser
        // Then re-think dependencyAdder
        createElementTypeParser(() -> new YarnLockEntryHeaderParser(yarnLockLineAnalyzer));
        createElementTypeParser(() -> new YarnLockDependencyListElementParser(yarnLockLineAnalyzer, yarnLockDependencySpecParser));
        createElementTypeParser(() -> new YarnLockDependencyMetaListElementParser(yarnLockLineAnalyzer, yarnLockDependencySpecParser));
        //createElementTypeParser(() -> new YarnLockListElementParser(yarnLockLineAnalyzer, "Build Requires", YarnLockEntryBuilder::addBuildRequiresRef));
        createElementTypeParser(() -> new YarnLockKeyValuePairElementParser(yarnLockLineAnalyzer, "version", YarnLockEntryBuilder::setVersion));
        //        createElementTypeParser(() -> new YarnLockKeyValuePairElementParser(yarnLockLineAnalyzer, "Revision", YarnLockEntryBuilder::setRecipeRevision));
        //        createElementTypeParser(() -> new YarnLockKeyValuePairElementParser(yarnLockLineAnalyzer, "Package revision", YarnLockEntryBuilder::setPackageRevision));
    }

    private void createElementTypeParser(Supplier<YarnLockElementTypeParser> elementSupplier) {
        elementParsers.add(elementSupplier.get());
    }

    public int parseElement(YarnLockEntryBuilder entryBuilder, List<String> conanInfoOutputLines, int bodyElementLineIndex) {
        String line = conanInfoOutputLines.get(bodyElementLineIndex);
        if (line.startsWith("#") || line.trim().isEmpty()) {
            return bodyElementLineIndex;
        }
        return elementParsers.stream()
                   .filter(ep -> ep.applies(line))
                   .findFirst()
                   .map(ep -> ep.parseElement(entryBuilder, conanInfoOutputLines, bodyElementLineIndex))
                   .orElse(bodyElementLineIndex);
    }
}
