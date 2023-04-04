package com.synopsys.integration.detectable.detectables.cocoapods.functional;

import java.io.IOException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectables.cocoapods.parser.PodlockParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphCompare;

@FunctionalTest
public class CocoapodsPackagerTest {
    private final PodlockParser podlockParser = new PodlockParser(new ExternalIdFactory());

    @Test
    @Disabled
    public void simpleTest() throws IOException, MissingExternalIdException {
        String podlockText = FunctionalTestFiles.asString("/cocoapods/simplePodfile.lock");
        DependencyGraph projectDependencies = podlockParser.extractDependencyGraph(podlockText);
        GraphCompare.assertEqualsResource("/cocoapods/simpleExpected_graph.json", projectDependencies);
    }

    @Test
    @Disabled
    public void complexTest() throws IOException, MissingExternalIdException {
        String podlockText = FunctionalTestFiles.asString("/cocoapods/complexPodfile.lock");
        DependencyGraph projectDependencies = podlockParser.extractDependencyGraph(podlockText);
        GraphCompare.assertEqualsResource("/cocoapods/complexExpected_graph.json", projectDependencies);
    }
    
    @Test
    public void noPodsTest() throws IOException, MissingExternalIdException {
        String podlockText = FunctionalTestFiles.asString("/cocoapods/nopodsPodfile.lock");
        DependencyGraph projectDependencies = podlockParser.extractDependencyGraph(podlockText);
        GraphCompare.assertEqualsResource("/cocoapods/noPodsExpected_graph.json", projectDependencies);
    }
}
