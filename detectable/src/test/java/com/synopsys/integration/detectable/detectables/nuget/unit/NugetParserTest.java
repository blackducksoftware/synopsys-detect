package com.synopsys.integration.detectable.detectables.nuget.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.nuget.model.NugetInspection;
import com.synopsys.integration.detectable.detectables.nuget.parse.NugetInspectorParser;
import com.synopsys.integration.detectable.detectables.nuget.parse.NugetParseResult;

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
}
