package com.synopsys.integration.detectable.detectables.conan.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.conan.ConanCodeLocationGenerator;
import com.synopsys.integration.detectable.detectables.conan.ConanDetectableResult;
import com.synopsys.integration.detectable.detectables.conan.cli.config.ConanDependencyType;
import com.synopsys.integration.detectable.detectables.conan.cli.parser.ConanInfoLineAnalyzer;
import com.synopsys.integration.detectable.detectables.conan.cli.parser.ConanInfoNodeParser;
import com.synopsys.integration.detectable.detectables.conan.cli.parser.ConanInfoParser;
import com.synopsys.integration.detectable.detectables.conan.cli.parser.element.NodeElementParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphCompare;
import com.synopsys.integration.exception.IntegrationException;

public class ConanCliParserFunctionalTest {

    @Test
    public void test() throws IOException, IntegrationException {
        File conanInfoOutputFile = FunctionalTestFiles.asFile("/conan/cli/conan_info.txt");
        EnumListFilter<ConanDependencyType> dependencyTypeFilter = EnumListFilter.excludeNone();
        ConanCodeLocationGenerator conanCodeLocationGenerator = new ConanCodeLocationGenerator(dependencyTypeFilter, false);
        ConanInfoLineAnalyzer conanInfoLineAnalyzer = new ConanInfoLineAnalyzer();
        NodeElementParser nodeElementParser = new NodeElementParser(conanInfoLineAnalyzer);
        ConanInfoNodeParser conanInfoNodeParser = new ConanInfoNodeParser(conanInfoLineAnalyzer, nodeElementParser);
        ConanInfoParser parser = new ConanInfoParser(conanInfoNodeParser, conanCodeLocationGenerator, new ExternalIdFactory());
        String conanInfoOutput = FileUtils.readFileToString(conanInfoOutputFile, StandardCharsets.UTF_8);

        ConanDetectableResult result = parser.generateCodeLocationFromConanInfoOutput(conanInfoOutput);

        assertEquals(3, result.getCodeLocation().getDependencyGraph().getRootDependencies().size());
        DependencyGraph actualDependencyGraph = result.getCodeLocation().getDependencyGraph();
        GraphCompare.assertEqualsResource("/conan/cli/noProjectRef_graph.json", actualDependencyGraph);
    }
}
