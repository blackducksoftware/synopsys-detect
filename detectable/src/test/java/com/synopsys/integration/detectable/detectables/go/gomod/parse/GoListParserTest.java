package com.synopsys.integration.detectable.detectables.go.gomod.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectables.go.gomod.model.GoListAllData;
import com.synopsys.integration.detectable.detectables.go.gomod.model.GoListModule;
import com.synopsys.integration.detectable.detectables.go.gomod.model.ReplaceData;

class GoListParserTest {
    private static GoListParser goListParser;

    @BeforeAll
    static void init() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        goListParser = new GoListParser(gson);
    }

    @Test
    void goListHappyPathTest() throws DetectableException {
        List<String> goListOutput = Arrays.asList(
            "{\n",
            "\t\"Path\": \"github.com/synopsys/moduleA\"\n",
            "}\n",
            "{\n",
            "\t\"Path\": \"github.com/synopsys/moduleB\",\n",
            "\t\"Version\": \"1.0.0\"\n",
            "}\n"
        );

        List<GoListModule> goListModules = goListParser.parseGoListModuleJsonOutput(goListOutput);
        assertEquals(2, goListModules.size());

        GoListModule moduleA = goListModules.get(0);
        assertEquals("github.com/synopsys/moduleA", moduleA.getPath());
        assertNull(moduleA.getVersion());

        GoListModule moduleB = goListModules.get(1);
        assertEquals("github.com/synopsys/moduleB", moduleB.getPath());
        assertEquals("1.0.0", moduleB.getVersion());
    }

    @Test
    void goListAllHappyPathTest() throws DetectableException {
        List<String> goListAllOutput = Arrays.asList(
            "{\n",
            "\t\"Path\": \"github.com/synopsys/moduleA\",\n",
            "\t\"Version\": \"v1.0.0\"\n",
            "}\n",
            "{\n",
            "\t\"Path\": \"github.com/synopsys/moduleB\",\n",
            "\t\"Version\": \"v2.0.0\",\n",
            "\t\"Replace\": {\n",
            "\t\t\"Path\": \"github.com/synopsys/moduleB\",\n",
            "\t\t\"Version\": \"v3.0.0\"\n",
            "\t}\n",
            "}"
        );

        List<GoListAllData> goListModules = goListParser.parseGoListAllJsonOutput(goListAllOutput);
        assertEquals(2, goListModules.size());

        GoListAllData moduleA = goListModules.get(0);
        assertEquals("github.com/synopsys/moduleA", moduleA.getPath());
        assertEquals("v1.0.0", moduleA.getVersion());
        assertNull(moduleA.getReplace());

        GoListAllData moduleB = goListModules.get(1);
        assertEquals("github.com/synopsys/moduleB", moduleB.getPath());
        assertEquals("v2.0.0", moduleB.getVersion());

        ReplaceData replaceData = moduleB.getReplace();
        assertEquals("github.com/synopsys/moduleB", replaceData.getPath());
        assertEquals("v3.0.0", replaceData.getVersion());
    }

    @Test
    void invalidJsonTest() {
        List<String> goListOutput = Arrays.asList(
            "{",
            "This could be malformed json.",
            "}"
        );
        assertThrows(JsonSyntaxException.class, () -> goListParser.parseGoListModuleJsonOutput(goListOutput));
        assertThrows(JsonSyntaxException.class, () -> goListParser.parseGoListAllJsonOutput(goListOutput));
    }

}
