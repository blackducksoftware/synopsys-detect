package com.blackducksoftware.integration.hub.detect.bomtool.nuget

import static org.junit.Assert.assertEquals

import org.junit.Test

import com.blackducksoftware.integration.hub.bdio.BdioNodeFactory
import com.blackducksoftware.integration.hub.bdio.BdioPropertyHelper
import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraphTransformer
import com.blackducksoftware.integration.hub.bdio.model.BdioComponent
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.nuget.parse.NugetInspectorPackager
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.nuget.parse.NugetParseResult
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.google.gson.Gson
import com.google.gson.GsonBuilder

public class NugetInspectorPackagerPerfTest {
    public Gson gson = new GsonBuilder().setPrettyPrinting().create()
    public ExternalIdFactory externalIdFactory = new ExternalIdFactory()

    @Test(timeout = 120000L)
    public void performanceTestNuget() throws IOException {
        def dependencyGraphFile = new File(getClass().getResource("/nuget/dwCheckApi_inspection.json").getFile())

        def packager = new NugetInspectorPackager()

        packager.gson = new Gson()
        packager.nameVersionNodeTransformer = new NameVersionNodeTransformer(new ExternalIdFactory())
        packager.externalIdFactory = new ExternalIdFactory()

        NugetParseResult result = packager.createDetectCodeLocation(dependencyGraphFile)
        DetectCodeLocation codeLocation = result.codeLocations[0]

        final BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper()
        final BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper)
        final DependencyGraphTransformer dependencyGraphTransformer = new DependencyGraphTransformer(bdioPropertyHelper, bdioNodeFactory)

        final bdioNode = bdioNodeFactory.createProject("test", "1.0.0", "bdioId", externalIdFactory.createMavenExternalId("group", "name", "version"))

        final List<BdioComponent> components = dependencyGraphTransformer.transformDependencyGraph(codeLocation.dependencyGraph, bdioNode, codeLocation.dependencyGraph.rootDependencies, [:])

        assertEquals(211, components.size())
    }
}
