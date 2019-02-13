package com.synopsys.integration.detect.detector.nuget;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.BdioNodeFactory;
import com.synopsys.integration.bdio.BdioPropertyHelper;
import com.synopsys.integration.bdio.graph.DependencyGraphTransformer;
import com.synopsys.integration.bdio.model.BdioComponent;
import com.synopsys.integration.bdio.model.BdioNode;
import com.synopsys.integration.bdio.model.BdioProject;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class NugetInspectorPackagerPerfTest {
    public Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public ExternalIdFactory externalIdFactory = new ExternalIdFactory();

    @Test(timeout = 120000L)
    public void performanceTestNuget() throws IOException {
        final File dependencyGraphFile = new File(getClass().getResource("/nuget/dwCheckApi_inspection.json").getFile());

        final NugetInspectorPackager packager = new NugetInspectorPackager(gson, externalIdFactory);

        final NugetParseResult result = packager.createDetectCodeLocation(dependencyGraphFile);
        final DetectCodeLocation codeLocation = result.codeLocations.get(0);

        final BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();
        final BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper);
        final DependencyGraphTransformer dependencyGraphTransformer = new DependencyGraphTransformer(bdioPropertyHelper, bdioNodeFactory);

        final BdioProject bdioNode = bdioNodeFactory.createProject("test", "1.0.0", "bdioId", externalIdFactory.createMavenExternalId("group", "name", "version"));

        final List<BdioComponent> components = dependencyGraphTransformer.transformDependencyGraph(codeLocation.getDependencyGraph(), bdioNode, codeLocation.getDependencyGraph().getRootDependencies(), new HashMap<ExternalId, BdioNode>());

        assertEquals(211, components.size());
    }
}
