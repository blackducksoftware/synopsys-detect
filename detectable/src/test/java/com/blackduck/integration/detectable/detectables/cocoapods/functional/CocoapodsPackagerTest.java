package com.blackduck.integration.detectable.detectables.cocoapods.functional;

import java.io.IOException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.blackduck.integration.bdio.graph.DependencyGraph;
import com.blackduck.integration.bdio.graph.builder.MissingExternalIdException;
import com.blackduck.integration.bdio.model.externalid.ExternalIdFactory;
import com.blackduck.integration.detectable.annotations.FunctionalTest;
import com.blackduck.integration.detectable.detectables.cocoapods.parser.PodlockParser;
import com.blackduck.integration.detectable.util.FunctionalTestFiles;
import com.blackduck.integration.detectable.util.GraphCompare;

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
