package com.blackduck.integration.detectable.detectables.nuget.unit;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.blackduck.integration.detectable.detectables.nuget.model.NugetContainer;
import com.blackduck.integration.detectable.detectables.nuget.model.NugetContainerType;
import com.blackduck.integration.detectable.detectables.nuget.model.NugetInspection;
import com.blackduck.integration.detectable.detectables.nuget.parse.NugetInspectorParser;
import com.blackduck.integration.detectable.detectables.nuget.parse.NugetParseResult;

public class NugetParserTest {
    Gson gson = new Gson();

    @Test
    public void handlesNullContainer() {
        NugetInspection result = new NugetInspection();
        result.containers.add(null);
        String resultText = gson.toJson(result);

        NugetInspectorParser parser = new NugetInspectorParser(gson, new ExternalIdFactory());
        NugetParseResult parsed = parser.createCodeLocation(resultText);

        Assertions.assertEquals(0, parsed.getCodeLocations().size());

    }

    @Test
    public void handlesNullChild() {
        NugetInspection result = new NugetInspection();
        NugetContainer container = new NugetContainer();
        container.type = NugetContainerType.SOLUTION;
        container.children = new ArrayList<>();
        container.children.add(null);
        result.containers.add(container);
        String resultText = gson.toJson(result);

        NugetInspectorParser parser = new NugetInspectorParser(gson, new ExternalIdFactory());
        NugetParseResult parsed = parser.createCodeLocation(resultText);

        Assertions.assertEquals(0, parsed.getCodeLocations().size());
    }
}
