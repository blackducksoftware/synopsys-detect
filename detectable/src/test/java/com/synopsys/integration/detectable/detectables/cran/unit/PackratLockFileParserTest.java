package com.synopsys.integration.detectable.detectables.cran.unit;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.cran.parse.PackratLockFileParser;
import com.synopsys.integration.detectable.util.graph.GraphAssert;

class PackratLockFileParserTest {
    private ExternalIdFactory externalIdFactory;
    private PackratLockFileParser packratLockFileParser;
    private List<String> packratFileLines;

    @BeforeEach
    void setUp() {
        externalIdFactory = new ExternalIdFactory();
        packratLockFileParser = new PackratLockFileParser(externalIdFactory);

        packratFileLines = new ArrayList<>();
        packratFileLines.add("This is a bogus line");
        packratFileLines.add("Package: misc");
        packratFileLines.add("Source: CRAN");
        packratFileLines.add("Version: 1.11");
        packratFileLines.add("Hash: c9b8888a595ca8153ee1ed47bd8f771c");
        packratFileLines.add("Requires: taken, check,");
        packratFileLines.add("    checkmate");
        packratFileLines.add("");
        packratFileLines.add("Package: checkmate");
        packratFileLines.add("Source: CRAN");
        packratFileLines.add("Version: 1.8.5");
        packratFileLines.add("Hash: e1bbc5228ab3da931a099208bc95ad23");
        packratFileLines.add("");
        packratFileLines.add("Package: taken");
        packratFileLines.add("Source: CRAN");
        packratFileLines.add("Version: 1.9.5");
        packratFileLines.add("Hash: e1bbc5228ab3da931a099208bc95ad23");
        packratFileLines.add("");
        packratFileLines.add("Package: check");
        packratFileLines.add("Source: CRAN");
        packratFileLines.add("Version: 2.0.0");
        packratFileLines.add("Hash: e1bbc5228ab3da931a099208bc95ad23");
    }

    @Test
    void parseProjectDependencies() {
        final DependencyGraph dependencyGraph = packratLockFileParser.parseProjectDependencies(packratFileLines);
        final GraphAssert graphAssert = new GraphAssert(Forge.CRAN, dependencyGraph);

        final ExternalId misc = externalIdFactory.createNameVersionExternalId(Forge.CRAN, "misc", "1.11");
        final ExternalId checkmate = externalIdFactory.createNameVersionExternalId(Forge.CRAN, "checkmate", "1.8.5");
        final ExternalId taken = externalIdFactory.createNameVersionExternalId(Forge.CRAN, "taken", "1.9.5");
        final ExternalId check = externalIdFactory.createNameVersionExternalId(Forge.CRAN, "check", "2.0.0");

        graphAssert.hasDependency(misc);
        graphAssert.hasDependency(checkmate);
        graphAssert.hasDependency(taken);
        graphAssert.hasDependency(check);

        graphAssert.hasParentChildRelationship(misc, taken);
        graphAssert.hasParentChildRelationship(misc, check);
        graphAssert.hasParentChildRelationship(misc, checkmate);
    }
}
