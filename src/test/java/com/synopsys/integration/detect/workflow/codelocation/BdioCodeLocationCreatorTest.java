package com.synopsys.integration.detect.workflow.codelocation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.util.NameVersion;

public class BdioCodeLocationCreatorTest {

    // TODO: This test seems suspiciously long and like it might not be testing much. -jp
    @Test
    public void testCreateFromDetectCodeLocations() throws IOException, DetectUserFriendlyException {

        File sourceDir = new File("src/test/resource");

        CodeLocationNameManager codeLocationNameManager = Mockito.mock(CodeLocationNameManager.class);
        DirectoryManager directoryManager = Mockito.mock(DirectoryManager.class);
        Mockito.when(directoryManager.getSourceDirectory()).thenReturn(sourceDir);
        EventSystem eventSystem = Mockito.mock(EventSystem.class);
        CreateBdioCodeLocationsFromDetectCodeLocationsOperation creator = new CreateBdioCodeLocationsFromDetectCodeLocationsOperation(codeLocationNameManager, directoryManager);
        NameVersion projectNameVersion = new NameVersion("testName", "testVersion");
        DependencyGraph dependencyGraph = Mockito.mock(DependencyGraph.class);
        Set<Dependency> dependencies = new HashSet<>();
        Dependency dependency = Mockito.mock(Dependency.class);
        dependencies.add(dependency);
        Mockito.when(dependencyGraph.getRootDependencies()).thenReturn(dependencies);

        ExternalId externalId = new ExternalId(Forge.MAVEN);
        externalId.setName("testExternalIdName");
        externalId.setVersion("testExternalIdVersion");
        externalId.setArchitecture("testExternalIdArch");
        DetectCodeLocation detectCodeLocation = DetectCodeLocation.forCreator(dependencyGraph, sourceDir, externalId, "testCreator");
        List<DetectCodeLocation> detectCodeLocations = new ArrayList<>();
        detectCodeLocations.add(detectCodeLocation);
        Mockito.when(codeLocationNameManager.createCodeLocationName(detectCodeLocation, sourceDir, projectNameVersion.getName(), projectNameVersion.getVersion(), "", ""))
            .thenReturn("testCodeLocationName");

        BdioCodeLocationResult result = creator.transformDetectCodeLocations(detectCodeLocations, "", "", projectNameVersion);

        assertEquals("testCodeLocationName", result.getBdioCodeLocations().get(0).getCodeLocationName());
        File resultDir = result.getBdioCodeLocations().get(0).getDetectCodeLocation().getSourcePath();
        assertTrue(resultDir.getCanonicalPath().contains("test"));
        assertTrue(resultDir.getCanonicalPath().contains("resource"));
    }
}
