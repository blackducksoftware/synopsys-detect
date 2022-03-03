package com.synopsys.integration.detectable.detectables.nuget.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.BdioNodeFactory;
import com.synopsys.integration.bdio.BdioPropertyHelper;
import com.synopsys.integration.bdio.graph.DependencyGraphTransformer;
import com.synopsys.integration.bdio.model.BdioComponent;
import com.synopsys.integration.bdio.model.BdioId;
import com.synopsys.integration.bdio.model.BdioProject;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.nuget.parse.NugetInspectorParser;
import com.synopsys.integration.detectable.detectables.nuget.parse.NugetParseResult;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;

public class NugetInspectorParserPerfTest {
    public Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public ExternalIdFactory externalIdFactory = new ExternalIdFactory();

    @Disabled
    @Test
    public void performanceTestNuget() {
        Assertions.assertTimeout(Duration.ofSeconds(120), () -> {
            String dependencyGraphFile = FunctionalTestFiles.asString("/nuget/dwCheckApi_inspection.json");

            NugetInspectorParser packager = new NugetInspectorParser(gson, externalIdFactory);

            NugetParseResult result = packager.createCodeLocation(dependencyGraphFile);
            CodeLocation codeLocation = result.getCodeLocations().get(0);

            BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();
            BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper);
            DependencyGraphTransformer dependencyGraphTransformer = new DependencyGraphTransformer(bdioPropertyHelper, bdioNodeFactory);

            BdioProject bdioNode = bdioNodeFactory.createProject(
                "test",
                "1.0.0",
                BdioId.createFromPieces("bdioId"),
                externalIdFactory.createMavenExternalId("group", "name", "version")
            );

            List<BdioComponent> components = dependencyGraphTransformer
                .transformDependencyGraph(codeLocation.getDependencyGraph(), bdioNode, codeLocation.getDependencyGraph().getRootDependencies(), new HashMap<>());

            assertEquals(211, components.size());
        });
    }
}
