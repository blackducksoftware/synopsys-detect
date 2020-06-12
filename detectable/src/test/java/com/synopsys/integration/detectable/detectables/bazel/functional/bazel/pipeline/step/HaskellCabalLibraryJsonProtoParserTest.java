package com.synopsys.integration.detectable.detectables.bazel.functional.bazel.pipeline.step;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.HaskellCabalLibraryJsonProtoParser;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class HaskellCabalLibraryJsonProtoParserTest {

    @Test
    public void test() throws IOException, IntegrationException {
        File jsonProtoFile = new File("src/test/resources/detectables/functional/bazel/jsonProtoForHaskellCabalLibraries.txt");
        String jsonProtoHaskellCabalLibrary = FileUtils.readFileToString(jsonProtoFile, StandardCharsets.UTF_8);

        HaskellCabalLibraryJsonProtoParser parser = new HaskellCabalLibraryJsonProtoParser(new Gson());

        List<NameVersion> dependencies = parser.parse(jsonProtoHaskellCabalLibrary);
        assertEquals(1, dependencies.size());
        assertEquals("colour", dependencies.get(0).getName());
        assertEquals("2.3.5", dependencies.get(0).getVersion());
    }
}
