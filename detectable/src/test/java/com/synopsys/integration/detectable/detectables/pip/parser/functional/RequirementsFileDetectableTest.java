package com.synopsys.integration.detectable.detectables.pip.parser.functional;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectables.pip.parser.RequirementsFileDependencyTransformer;
import com.synopsys.integration.detectable.detectables.pip.parser.RequirementsFileExtractor;
import com.synopsys.integration.detectable.detectables.pip.parser.RequirementsFileTransformer;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

@FunctionalTest
class RequirementsFileDetectableTest {
    private static File requirementsFile;
    private static RequirementsFileExtractor requirementsFileExtractor;
    private static List<ExternalId> testDependencies = new ArrayList<>();
    private static ExternalId testDependency1;
    private static ExternalId testDependency2;
    private static ExternalId testDependency3;
    private static ExternalId testDependency4;
    private static ExternalId testDependency5;
    private static ExternalId testDependency8;
    private static ExternalId testDependency9;
    private static ExternalId testDependency10;
    private static ExternalId testDependency11;
    private static ExternalId testDependency12;
    private static ExternalId testDependency13;
    private static ExternalId testDependency14;
    private static ExternalId testDependency15;
    private static ExternalId testDependency16;
    private static ExternalId testDependency17;
    private static ExternalId testDependency18;
    private static ExternalId testDependency19;
    @BeforeAll
    protected static void setUp() {
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        testDependencies.add(externalIdFactory.createNameVersionExternalId(Forge.PYPI, "name1", ""));
        testDependencies.add(externalIdFactory.createNameVersionExternalId(Forge.PYPI, "name2", "1.2.3"));
        testDependencies.add(externalIdFactory.createNameVersionExternalId(Forge.PYPI, "name3", "1.2"));
        testDependencies.add(externalIdFactory.createNameVersionExternalId(Forge.PYPI, "name4", "0.5.3"));
        testDependencies.add(externalIdFactory.createNameVersionExternalId(Forge.PYPI, "name5", "1.2.3"));
        testDependencies.add(externalIdFactory.createNameVersionExternalId(Forge.PYPI, "name8", "1.2.3"));
        testDependencies.add(externalIdFactory.createNameVersionExternalId(Forge.PYPI, "name9", "1.2.3"));
        testDependencies.add(externalIdFactory.createNameVersionExternalId(Forge.PYPI, "name10", "1.2.3"));
        testDependencies.add(externalIdFactory.createNameVersionExternalId(Forge.PYPI, "name11", "1.2.3"));
        testDependencies.add(externalIdFactory.createNameVersionExternalId(Forge.PYPI, "name12", "1.2.3"));
        testDependencies.add(externalIdFactory.createNameVersionExternalId(Forge.PYPI, "name13", "1.2.3"));
        testDependencies.add(externalIdFactory.createNameVersionExternalId(Forge.PYPI, "name14", "1.2.3"));
        testDependencies.add(externalIdFactory.createNameVersionExternalId(Forge.PYPI, "name15", "1.3.0"));
        testDependencies.add(externalIdFactory.createNameVersionExternalId(Forge.PYPI, "name16", "1.2.3"));
        testDependencies.add(externalIdFactory.createNameVersionExternalId(Forge.PYPI, "name17", "1.2.3"));
        testDependencies.add(externalIdFactory.createNameVersionExternalId(Forge.PYPI, "name18", "1.2.3"));
        testDependencies.add(externalIdFactory.createNameVersionExternalId(Forge.PYPI, "name19", "1.2.3"));

        requirementsFile = new File("src/test/resources/detectables/functional/pip/requirements.txt");

        RequirementsFileTransformer requirementsFileTransformer = new RequirementsFileTransformer();
        RequirementsFileDependencyTransformer requirementsFileDependencyTransformer = new RequirementsFileDependencyTransformer();
        requirementsFileExtractor = new RequirementsFileExtractor(requirementsFileTransformer, requirementsFileDependencyTransformer);
    }

    @Test
    void testDependencyExtractionFromRequirementsFile() throws IOException {
        Set<File> requirementsFiles = Collections.singleton(requirementsFile);
        Extraction testFileExtraction = requirementsFileExtractor.extract(requirementsFiles);
        Assertions.assertEquals(1, testFileExtraction.getCodeLocations().size());

        DependencyGraph testDependencyGraph = testFileExtraction.getCodeLocations().get(0).getDependencyGraph();

        NameVersionGraphAssert nameVersionGraphAssert = new NameVersionGraphAssert(Forge.PYPI, testDependencyGraph);

        for (ExternalId testDependency: testDependencies) {
            nameVersionGraphAssert.hasDependency(testDependency.getName(), testDependency.getVersion());
        }

    }
}
